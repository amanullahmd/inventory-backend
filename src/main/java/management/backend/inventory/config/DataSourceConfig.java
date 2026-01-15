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
 * Parses DATABASE_URL (postgresql://user:pass@host:port/db) and converts to JDBC format.
 * Handles Railway's potentially malformed URLs where host might be missing.
 * Falls back to individual PG* variables.
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        log.info("=== DataSource Configuration ===");
        log.info("DATABASE_URL: {}", databaseUrl != null ? databaseUrl : "NOT SET");
        
        String jdbcUrl;
        String username;
        String password;
        
        if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("postgresql://")) {
            // Parse DATABASE_URL using regex to handle malformed URLs
            // Format: postgresql://user:pass@host:port/database
            // Railway sometimes has: postgresql://user:pass@:/database (missing host)
            
            // Pattern to extract: user, password, host (optional), port (optional), database
            Pattern pattern = Pattern.compile("postgresql://([^:]+):([^@]+)@([^:/]*):?(\\d*)/(.+)");
            Matcher matcher = pattern.matcher(databaseUrl);
            
            if (matcher.matches()) {
                username = matcher.group(1);
                String parsedPassword = matcher.group(2);
                String host = matcher.group(3);
                String portStr = matcher.group(4);
                String database = matcher.group(5);
                
                int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 5432;
                
                log.info("Regex parsed - User: {}, Password length: {}, Host: '{}', Port: {}, Database: {}", 
                        username, parsedPassword != null ? parsedPassword.length() : 0, host, port, database);
                
                // Handle missing host - use PGHOST or Railway internal networking
                if (host == null || host.isEmpty()) {
                    host = System.getenv("PGHOST");
                    log.info("Host empty in URL, PGHOST: {}", host);
                    if (host == null || host.isEmpty()) {
                        host = "postgres.railway.internal";
                        log.info("Using Railway internal networking: {}", host);
                    }
                }
                
                // Use password from DATABASE_URL (it's the source of truth from Railway)
                password = parsedPassword;
                log.info("Using password from DATABASE_URL (length={})", password != null ? password.length() : 0);
                
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
            } else {
                log.warn("DATABASE_URL doesn't match expected pattern, falling back to PG variables");
                return createFromPgVariables();
            }
        } else {
            log.info("DATABASE_URL not set or invalid, using PG variables");
            return createFromPgVariables();
        }
        
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
        
        if (pgHost == null || pgHost.isEmpty()) pgHost = "localhost";
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
