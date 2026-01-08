package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * DataSource Configuration
 * 
 * Configures HikariCP connection pool for PostgreSQL database.
 * Reads Railway PostgreSQL environment variables for production deployment.
 * Falls back to localhost for local development.
 * 
 * Spring Boot auto-configuration handles HikariCP setup based on application.yml
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * Configure DataSource using Spring Boot's auto-configuration
     * 
     * Spring Boot will automatically:
     * - Read spring.datasource.url, username, password from application.yml
     * - Use HikariCP as the connection pool (default)
     * - Apply hikari settings from application.yml
     * 
     * @return Configured DataSource with connection pooling
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        log.info("Initializing DataSource with Spring Boot auto-configuration");
        return DataSourceBuilder.create().build();
    }
}
