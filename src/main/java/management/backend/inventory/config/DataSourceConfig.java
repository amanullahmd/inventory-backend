package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * DataSource Configuration
 * 
 * Configures HikariCP connection pool for PostgreSQL database.
 * Optimized for Railway deployment with proper timeout and pool settings.
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * Configure HikariCP DataSource
     * 
     * @return Configured DataSource with connection pooling
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        // Get Railway PostgreSQL environment variables
        String host = System.getenv("RAILWAY_POSTGRESQL_HOST");
        String port = System.getenv("RAILWAY_POSTGRESQL_PORT");
        String database = System.getenv("RAILWAY_POSTGRESQL_DATABASE");
        String username = System.getenv("RAILWAY_POSTGRESQL_USER");
        String password = System.getenv("RAILWAY_POSTGRESQL_PASSWORD");
        
        // Use defaults for local development if Railway vars not set
        if (host == null || host.isEmpty()) {
            host = "localhost";
            log.warn("RAILWAY_POSTGRESQL_HOST not set, using default: {}", host);
        }
        if (port == null || port.isEmpty()) {
            port = "5432";
            log.warn("RAILWAY_POSTGRESQL_PORT not set, using default: {}", port);
        }
        if (database == null || database.isEmpty()) {
            database = "inventory";
            log.warn("RAILWAY_POSTGRESQL_DATABASE not set, using default: {}", database);
        }
        if (username == null || username.isEmpty()) {
            username = "user";
            log.warn("RAILWAY_POSTGRESQL_USER not set, using default: {}", username);
        }
        if (password == null || password.isEmpty()) {
            password = "password";
            log.warn("RAILWAY_POSTGRESQL_PASSWORD not set, using default");
        }
        
        // Construct JDBC URL from Railway environment variables
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        
        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);      // 30 seconds
        config.setIdleTimeout(600000);           // 10 minutes
        config.setMaxLifetime(1800000);          // 30 minutes
        config.setAutoCommit(true);
        config.setLeakDetectionThreshold(60000); // 1 minute
        
        // Driver class
        config.setDriverClassName("org.postgresql.Driver");
        
        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        
        // Pool name for logging
        config.setPoolName("InventoryDB-HikariPool");
        
        log.info("Configuring HikariCP DataSource");
        log.info("  JDBC URL: {}", jdbcUrl);
        log.info("  Host: {}", host);
        log.info("  Port: {}", port);
        log.info("  Database: {}", database);
        log.info("  Username: {}", username);
        log.info("  Max Pool Size: {}", config.getMaximumPoolSize());
        log.info("  Min Idle: {}", config.getMinimumIdle());
        log.info("  Connection Timeout: {} ms", config.getConnectionTimeout());
        log.info("  Idle Timeout: {} ms", config.getIdleTimeout());
        log.info("  Max Lifetime: {} ms", config.getMaxLifetime());
        
        try {
            HikariDataSource dataSource = new HikariDataSource(config);
            log.info("HikariCP DataSource created successfully");
            return dataSource;
        } catch (Exception e) {
            log.error("Failed to create HikariCP DataSource", e);
            throw new RuntimeException("Failed to create DataSource", e);
        }
    }
}
