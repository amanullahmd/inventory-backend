package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * DataSource Configuration for Railway PostgreSQL
 * 
 * Railway provides RAILWAY_POSTGRESQL_URL as a complete PostgreSQL URI:
 * postgres://username:password@host:port/database
 * 
 * This configuration parses that URI and creates a HikariCP DataSource
 * with the correct JDBC URL format that Hikari expects:
 * jdbc:postgresql://host:port/database
 * 
 * Falls back to localhost for local development when RAILWAY_POSTGRESQL_URL is not set.
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * Creates HikariCP DataSource from Railway PostgreSQL URL
     * 
     * Parses RAILWAY_POSTGRESQL_URL (postgres://user:pass@host:port/db)
     * into JDBC format (jdbc:postgresql://host:port/db) with separate credentials
     */
    @Bean
    public DataSource dataSource() {
        String railwayUrl = System.getenv("RAILWAY_POSTGRESQL_URL");
        
        // If Railway URL is not set, let Spring Boot use default configuration
        if (railwayUrl == null || railwayUrl.isEmpty()) {
            log.info("RAILWAY_POSTGRESQL_URL not set, using default Spring Boot configuration");
            return null;
        }

        try {
            log.info("Parsing Railway PostgreSQL URL");
            
            // Parse the PostgreSQL URI
            URI dbUri = new URI(railwayUrl);
            
            // Extract credentials
            String userInfo = dbUri.getUserInfo();
            if (userInfo == null) {
                throw new RuntimeException("No user info in RAILWAY_POSTGRESQL_URL");
            }
            
            String[] credentials = userInfo.split(":");
            String username = credentials[0];
            String password = credentials.length > 1 ? credentials[1] : "";
            
            // Extract host and port
            String host = dbUri.getHost();
            int port = dbUri.getPort() != -1 ? dbUri.getPort() : 5432;
            
            // Extract database name (remove leading slash)
            String path = dbUri.getPath();
            String database = path != null && path.length() > 1 ? path.substring(1) : "railway";
            
            // Build JDBC URL
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            
            log.info("Creating HikariCP DataSource for Railway PostgreSQL");
            log.debug("Host: {}, Port: {}, Database: {}", host, port, database);
            
            // Create HikariCP configuration
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
            
            log.info("HikariCP DataSource created successfully for Railway");
            return new HikariDataSource(config);
            
        } catch (URISyntaxException e) {
            log.error("Failed to parse RAILWAY_POSTGRESQL_URL: {}", railwayUrl, e);
            throw new RuntimeException("Invalid RAILWAY_POSTGRESQL_URL format", e);
        } catch (Exception e) {
            log.error("Failed to create DataSource from Railway PostgreSQL URL", e);
            throw new RuntimeException("Failed to create DataSource", e);
        }
    }
}
