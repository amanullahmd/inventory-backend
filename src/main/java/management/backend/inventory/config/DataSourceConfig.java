package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * DataSource Configuration for Railway PostgreSQL
 * 
 * Parses DATABASE_URL (postgresql://user:pass@host:port/db) and converts to JDBC format.
 * Falls back to individual PG* variables or localhost defaults.
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        log.info("=== DataSource Configuration ===");
        log.info("DATABASE_URL: {}", databaseUrl != null ? "SET (length=" + databaseUrl.length() + ")" : "NOT SET");
        
        String jdbcUrl;
        String username;
        String password;
        
        if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("postgresql://")) {
            // Parse DATABASE_URL: postgresql://user:pass@host:port/database
            try {
                URI uri = new URI(databaseUrl.replace("postgresql://", "http://"));
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String database = uri.getPath().substring(1); // Remove leading /
                String userInfo = uri.getUserInfo();
                
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                } else {
                    username = userInfo != null ? userInfo : "postgres";
                    password = System.getenv("PGPASSWORD");
                    if (password == null) password = "";
                }
                
                // Handle case where host might be empty in the URL
                if (host == null || host.isEmpty()) {
                    // Try to get from PGHOST
                    host = System.getenv("PGHOST");
                    if (host == null || host.isEmpty()) {
                        // Use Railway's internal networking
                        host = "postgres.railway.internal";
                    }
                }
                
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                log.info("Parsed DATABASE_URL - Host: {}, Port: {}, Database: {}, User: {}", 
                        host, port, database, username);
                        
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL: {}", e.getMessage());
                // Fall back to defaults
                jdbcUrl = "jdbc:postgresql://localhost:5432/railway";
                username = "postgres";
                password = "password";
            }
        } else {
            // Fall back to individual PG variables
            String pgHost = System.getenv("PGHOST");
            String pgPort = System.getenv("PGPORT");
            String pgDatabase = System.getenv("PGDATABASE");
            
            if (pgHost == null || pgHost.isEmpty()) pgHost = "localhost";
            if (pgPort == null || pgPort.isEmpty()) pgPort = "5432";
            if (pgDatabase == null || pgDatabase.isEmpty()) pgDatabase = "railway";
            
            username = System.getenv("PGUSER");
            password = System.getenv("PGPASSWORD");
            if (username == null || username.isEmpty()) username = "postgres";
            if (password == null) password = "password";
            
            jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDatabase);
            
            log.info("Using PG variables - Host: {}, Port: {}, Database: {}", pgHost, pgPort, pgDatabase);
        }
        
        log.info("Final JDBC URL: {}", jdbcUrl);
        log.info("Username: {}", username);
        
        // Create HikariCP configuration
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Connection pool settings
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
