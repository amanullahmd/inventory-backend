package management.backend.inventory.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * DataSource Configuration for Railway PostgreSQL
 * 
 * Railway provides DATABASE_URL as a complete PostgreSQL URI:
 * postgresql://username:password@host:port/database
 * 
 * This configuration parses that URI and creates a HikariCP DataSource
 * with the correct JDBC URL format that Hikari expects:
 * jdbc:postgresql://host:port/database
 * 
 * Falls back to Spring Boot's default DataSource configuration when 
 * DATABASE_URL is not set (for local development).
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    /**
     * Creates HikariCP DataSource from Railway DATABASE_URL
     * 
     * This bean is only created when DATABASE_URL environment variable is set.
     * For local development without this variable, Spring Boot's auto-configuration
     * will create the DataSource from application.yml properties.
     * 
     * Parses DATABASE_URL (postgresql://user:pass@host:port/db)
     * into JDBC format (jdbc:postgresql://host:port/db) with separate credentials
     */
    @Bean
    @Primary
    @ConditionalOnExpression("#{environment.getProperty('DATABASE_URL') != null}")
    public DataSource railwayDataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            // This shouldn't happen due to @ConditionalOnExpression, but just in case
            log.warn("DATABASE_URL is empty, this bean should not have been created");
            return null;
        }

        try {
            log.info("Parsing DATABASE_URL: {}", databaseUrl.replaceAll(":[^:@]+@", ":****@"));
            
            // Handle both postgres:// and postgresql:// schemes
            String normalizedUrl = databaseUrl;
            if (normalizedUrl.startsWith("postgres://")) {
                normalizedUrl = "postgresql://" + normalizedUrl.substring("postgres://".length());
            }
            
            // Parse the PostgreSQL URI
            URI dbUri = new URI(normalizedUrl);
            
            // Extract credentials
            String userInfo = dbUri.getUserInfo();
            if (userInfo == null) {
                throw new RuntimeException("No user info in DATABASE_URL");
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
            
            log.info("Creating HikariCP DataSource for DATABASE_URL");
            log.info("Host: {}, Port: {}, Database: {}", host, port, database);
            
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
            
            log.info("HikariCP DataSource created successfully");
            return new HikariDataSource(config);
            
        } catch (URISyntaxException e) {
            log.error("Failed to parse DATABASE_URL: {}", databaseUrl.replaceAll(":[^:@]+@", ":****@"), e);
            throw new RuntimeException("Invalid DATABASE_URL format", e);
        } catch (Exception e) {
            log.error("Failed to create DataSource from DATABASE_URL", e);
            throw new RuntimeException("Failed to create DataSource", e);
        }
    }
}
