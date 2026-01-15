package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataSource Configuration for Railway PostgreSQL
 * 
 * Priority order for connection:
 * 1. DATABASE_PUBLIC_URL (if set - public proxy connection)
 * 2. DATABASE_URL with proper host parsing
 * 3. Individual PG* variables (PGHOST, PGPORT, etc.)
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("=== DataSource Configuration ===");
        
        // Log all available env vars for debugging
        String databaseUrl = System.getenv("DATABASE_URL");
        String databasePublicUrl = System.getenv("DATABASE_PUBLIC_URL");
        String pgHost = System.getenv("PGHOST");
        String pgPort = System.getenv("PGPORT");
        String pgDatabase = System.getenv("PGDATABASE");
        String pgUser = System.getenv("PGUSER");
        String pgPassword = System.getenv("PGPASSWORD");
        
        log.info("DATABASE_URL: {}", databaseUrl != null ? databaseUrl : "NOT SET");
        log.info("DATABASE_PUBLIC_URL: {}", databasePublicUrl != null ? "SET (length=" + databasePublicUrl.length() + ")" : "NOT SET");
        log.info("PGHOST: {}", pgHost);
        log.info("PGPORT: {}", pgPort);
        log.info("PGDATABASE: {}", pgDatabase);
        log.info("PGUSER: {}", pgUser);
        log.info("PGPASSWORD: {}", pgPassword != null ? "SET (length=" + pgPassword.length() + ")" : "NOT SET");
        
        // Try DATABASE_PUBLIC_URL first (has full host:port)
        if (databasePublicUrl != null && !databasePublicUrl.isEmpty()) {
            log.info("Trying DATABASE_PUBLIC_URL...");
            DataSource ds = tryParseUrl(databasePublicUrl);
            if (ds != null) return ds;
        }
        
        // Try DATABASE_URL 
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            log.info("Trying DATABASE_URL...");
            DataSource ds = tryParseUrl(databaseUrl);
            if (ds != null) return ds;
        }
        
        // Fall back to PG variables
        log.info("Falling back to PG variables...");
        return createFromPgVariables();
    }
    
    private DataSource tryParseUrl(String url) {
        if (!url.startsWith("postgresql://")) {
            log.warn("URL doesn't start with postgresql://");
            return null;
        }
        
        // Pattern: postgresql://user:pass@host:port/database
        // Also handles: postgresql://user:pass@host/database (no port)
        // Also handles: postgresql://user:pass@:port/database (no host - Railway bug)
        Pattern pattern = Pattern.compile("postgresql://([^:]+):([^@]+)@([^:/]*):?(\\d*)/(.+)");
        Matcher matcher = pattern.matcher(url);
        
        if (!matcher.matches()) {
            log.warn("URL doesn't match expected pattern: {}", url);
            return null;
        }
        
        String username = matcher.group(1);
        String password = matcher.group(2);
        String host = matcher.group(3);
        String portStr = matcher.group(4);
        String database = matcher.group(5);
        
        log.info("Parsed - User: {}, Password length: {}, Host: '{}', Port: '{}', Database: {}", 
                username, password != null ? password.length() : 0, host, portStr, database);
        
        // Handle missing host
        if (host == null || host.isEmpty()) {
            String pgHost = System.getenv("PGHOST");
            if (pgHost != null && !pgHost.isEmpty()) {
                host = pgHost;
                log.info("Using PGHOST for missing host: {}", host);
            } else {
                host = "postgres.railway.internal";
                log.info("Using Railway internal networking for missing host: {}", host);
            }
        }
        
        // Handle port - use PGPORT if not in URL
        int port;
        if (portStr != null && !portStr.isEmpty()) {
            port = Integer.parseInt(portStr);
        } else {
            String pgPort = System.getenv("PGPORT");
            if (pgPort != null && !pgPort.isEmpty()) {
                port = Integer.parseInt(pgPort);
                log.info("Using PGPORT for missing port: {}", port);
            } else {
                port = 5432;
            }
        }
        
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        log.info("Final JDBC URL: {}", jdbcUrl);
        log.info("Username: {}", username);
        
        return createDataSource(jdbcUrl, username, password);
    }
    
    private DataSource createFromPgVariables() {
        String pgHost = System.getenv("PGHOST");
        String pgPort = System.getenv("PGPORT");
        String pgDatabase = System.getenv("PGDATABASE");
        String username = System.getenv("PGUSER");
        String password = System.getenv("PGPASSWORD");
        
        if (pgHost == null || pgHost.isEmpty()) pgHost = "postgres.railway.internal";
        if (pgPort == null || pgPort.isEmpty()) pgPort = "5432";
        if (pgDatabase == null || pgDatabase.isEmpty()) pgDatabase = "railway";
        if (username == null || username.isEmpty()) username = "postgres";
        if (password == null) password = "";
        
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDatabase);
        
        log.info("Using PG variables - Host: {}, Port: {}, Database: {}, User: {}", 
                pgHost, pgPort, pgDatabase, username);
        log.info("Final JDBC URL: {}", jdbcUrl);
        
        return createDataSource(jdbcUrl, username, password);
    }
    
    private DataSource createDataSource(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Connection pool settings optimized for Railway
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(true);
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(60000);
        
        log.info("=== DataSource Created ===");
        return new HikariDataSource(config);
    }
}
