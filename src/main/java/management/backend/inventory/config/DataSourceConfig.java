package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * DataSource Configuration
 * 
 * Configures HikariCP connection pool for PostgreSQL database.
 * Reads Railway PostgreSQL environment variables for production deployment.
 * Falls back to localhost for local development.
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    // Railway provides RAILWAY_POSTGRESQL_URL as complete JDBC URL
    @Value("${RAILWAY_POSTGRESQL_URL:jdbc:postgresql://localhost:5432/inventory}")
    private String jdbcUrl;

    @Value("${RAILWAY_POSTGRESQL_USER:postgres}")
    private String username;

    @Value("${RAILWAY_POSTGRESQL_PASSWORD:postgres}")
    private String password;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    /**
     * Configure HikariCP DataSource
     * 
     * @return Configured DataSource with connection pooling
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        
        // Connection pool settings
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
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
        log.info("  Max Pool Size: {}", maxPoolSize);
        log.info("  Min Idle: {}", minIdle);
        log.info("  Connection Timeout: {} ms", connectionTimeout);
        log.info("  Idle Timeout: {} ms", idleTimeout);
        log.info("  Max Lifetime: {} ms", maxLifetime);
        
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
