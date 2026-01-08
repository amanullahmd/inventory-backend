package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration
 * 
 * Configures Cross-Origin Resource Sharing (CORS) to allow frontend access to backend API.
 * Configured for Railway deployment with environment-based frontend domain.
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configuring CORS");
        log.info("  Allowed Origins: {}", allowedOrigins);
        log.info("  Allowed Methods: {}", allowedMethods);
        log.info("  Allow Credentials: {}", allowCredentials);
        log.info("  Max Age: {} seconds", maxAge);

        registry.addMapping("/api/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders(allowedHeaders.split(","))
            .allowCredentials(allowCredentials)
            .maxAge(maxAge)
            .exposedHeaders("Authorization", "X-Total-Count", "X-Page-Number");

        // Allow health check endpoint
        registry.addMapping("/health")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods("GET")
            .maxAge(maxAge);

        // Allow actuator endpoints
        registry.addMapping("/actuator/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods("GET")
            .maxAge(maxAge);

        log.info("CORS configuration completed");
    }
}
