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
        
        // Get JDBC URL from environment or use default
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = "jdbc:postgresql://localhost:5432/inventory";
            log.warn("JDBC_DATABASE_URL not set, using default: {}", jdbcUrl);
        }
        config.setJdbcUrl(jdbcUrl);
        
        // Get credentials from environment or use defaults
        String username = System.getenv("PGUSER");
        if (username == null || username.isEmpty()) {
            username = "user";
            log.warn("PGUSER not set, using default: {}", username);
        }
        config.setUsername(username);
        
        String password = System.getenv("PGPASSWORD");
        if (password == null || password.isEmpty()) {
            password = "password";
            log.warn("PGPASSWORD not set, using default");
        }
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
