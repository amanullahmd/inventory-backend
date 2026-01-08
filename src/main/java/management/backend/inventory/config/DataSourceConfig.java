package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * DataSource Configuration for Railway PostgreSQL
 * 
 * Spring Boot 3.x auto-configures HikariCP DataSource from standard JDBC environment variables:
 * - JDBC_DATABASE_URL: jdbc:postgresql://host:port/database
 * - JDBC_DATABASE_USERNAME: database username
 * - JDBC_DATABASE_PASSWORD: database password
 * 
 * Railway provides RAILWAY_POSTGRESQL_URL (postgres://user:pass@host:port/db)
 * which must be converted to JDBC format by setting the above variables.
 * 
 * Spring Boot's DataSourceAutoConfiguration handles all the rest automatically.
 * No custom bean creation needed - just set the environment variables in Railway.
 */
@Slf4j
@Configuration
public class DataSourceConfig {
    // Spring Boot auto-configuration is sufficient
    // All datasource configuration is handled by Spring Boot based on:
    // 1. JDBC_DATABASE_URL environment variable (JDBC format)
    // 2. JDBC_DATABASE_USERNAME environment variable
    // 3. JDBC_DATABASE_PASSWORD environment variable
    // 4. application.yml and profile-specific files
    // 5. HikariCP connection pool settings
}
