package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DataSource Configuration
 * 
 * Spring Boot auto-configuration handles HikariCP setup based on application.yml
 * 
 * Reads Railway PostgreSQL environment variables for production deployment:
 * - DATABASE_URL: Complete JDBC URL (e.g., postgresql://user:pass@host:port/db)
 * - PGUSER: Database username
 * - PGPASSWORD: Database password
 * - PGHOST: Database host
 * - PGPORT: Database port
 * - PGDATABASE: Database name
 * 
 * Falls back to localhost for local development.
 * 
 * No custom bean needed - Spring Boot's DataSourceAutoConfiguration handles everything.
 * Configuration is defined in application.yml and profile-specific files.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
public class DataSourceConfig {
    // Spring Boot auto-configuration is sufficient
    // All datasource configuration is handled by Spring Boot based on:
    // 1. Environment variables (DATABASE_URL, PGUSER, PGPASSWORD)
    // 2. application.yml and profile-specific files (application-prod.yml, etc.)
    // 3. HikariCP connection pool settings
}
