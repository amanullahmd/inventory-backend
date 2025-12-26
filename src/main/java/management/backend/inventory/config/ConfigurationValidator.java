package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Validates application configuration at startup.
 * Fails fast if required configuration is missing or invalid.
 * 
 * Requirements: 7.3, 7.4
 */
@Slf4j
@Component
public class ConfigurationValidator {

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${app.security.jwt-secret-min-length:32}")
    private int jwtSecretMinLength;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * Validate configuration at application startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration() {
        log.info("Validating application configuration for profile: {}", activeProfile);

        // Validate JWT secret
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured. Set JWT_SECRET environment variable.");
        }

        // In production, enforce minimum JWT secret length
        if ("prod".equals(activeProfile) && jwtSecret.length() < jwtSecretMinLength) {
            throw new IllegalStateException(
                    String.format("JWT secret must be at least %d characters in production. Current length: %d",
                            jwtSecretMinLength, jwtSecret.length())
            );
        }

        log.info("Configuration validation passed");
    }
}
