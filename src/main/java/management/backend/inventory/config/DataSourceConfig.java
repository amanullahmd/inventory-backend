package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * DataSource Configuration for Railway PostgreSQL
 * 
 * Railway provides individual PostgreSQL variables:
 * - PGHOST: Database host
 * - PGPORT: Database port
 * - PGDATABASE: Database name
 * - PGUSER: Database username
 * - PGPASSWORD: Database password
 * 
 * This configuration uses these variables to create a proper JDBC connection.
 * Falls back to Spring Boot's default DataSource configuration when 
 * PGHOST is not set (for local development).
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * Creates HikariCP DataSource from Railway PG variables
     * 
     * This bean is only created when PGHOST environment variable is set.
     * For local development without this variable, Spring Boot's auto-configuration
     * will create the DataSource from application.yml properties.
     */
    @Bean
    @Primary
    @ConditionalOnExpression("#{environment.getProperty('PGHOST') != null && !environment.getProperty('PGHOST').isEmpty()}")
    public DataSource railwayDataSource() {
        String pgHost = System.getenv("PGHOST");
        String pgPort = System.getenv("PGPORT");
        String pgDatabase = System.getenv("PGDATABASE");
        String pgUser = System.getenv("PGUSER");
        String pgPassword = System.getenv("PGPASSWORD");
        
        // Validate required variables
        if (pgHost == null || pgHost.isEmpty()) {
            log.warn("PGHOST is empty, this bean should not have been created");
            return null;
        }

        // Use defaults for optional values
        if (pgPort == null || pgPort.isEmpty()) {
            pgPort = "5432";
        }
        if (pgDatabase == null || pgDatabase.isEmpty()) {
            pgDatabase = "railway";
        }
        if (pgUser == null || pgUser.isEmpty()) {
            pgUser = "postgres";
        }
        if (pgPassword == null) {
            pgPassword = "";
        }

        // Build JDBC URL
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDatabase);
        
        log.info("Creating HikariCP DataSource for Railway PostgreSQL");
        log.info("Host: {}, Port: {}, Database: {}, User: {}", pgHost, pgPort, pgDatabase, pgUser);
        
        // Create HikariCP configuration
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(pgUser);
        config.setPassword(pgPassword);
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
        
        log.info("HikariCP DataSource created successfully with JDBC URL: {}", jdbcUrl);
        return new HikariDataSource(config);
    }
}
