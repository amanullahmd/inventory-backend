package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;

/**
 * DataSource Configuration
 * 
 * Spring Boot auto-configuration handles HikariCP setup based on application.yml
 * 
 * Reads Railway PostgreSQL environment variables for production deployment:
 * - RAILWAY_POSTGRESQL_URL: Complete JDBC URL
 * - RAILWAY_POSTGRESQL_USER: Database username
 * - RAILWAY_POSTGRESQL_PASSWORD: Database password
 * 
 * Falls back to localhost for local development.
 * 
 * No custom bean needed - Spring Boot's DataSourceAutoConfiguration handles everything.
 */
@Slf4j
public class DataSourceConfig {
    // Spring Boot auto-configuration is sufficient
}
