package management.backend.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Validates application configuration at startup.
 * Fails fast if required configuration is missing or invalid in production.
 * 
 * Requirements: 7.3, 7.4
 */
@Slf4j
@Component
public class ConfigurationValidator {

    @Value("${jwt.secret:dev-secret-key-for-testing-only}")
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

        // Only enforce strict validation in production
        if ("prod".equals(activeProfile)) {
            validateProductionConfiguration();
        } else {
            log.warn("⚠️ Running in {} profile with development defaults", activeProfile);
        }

        log.info("✅ Configuration validation passed");
    }

    /**
     * Validate production configuration.
     */
    private void validateProductionConfiguration() {
        // Validate JWT secret is set and not default
        if (jwtSecret == null || jwtSecret.isEmpty() || jwtSecret.contains("dev-secret")) {
            throw new IllegalStateException(
                    "❌ CRITICAL: JWT_SECRET environment variable must be set in production. " +
                    "Set a strong, unique secret key."
            );
        }

        // Enforce minimum JWT secret length in production
        if (jwtSecret.length() < jwtSecretMinLength) {
            throw new IllegalStateException(
                    String.format("❌ JWT secret must be at least %d characters in production. Current length: %d",
                            jwtSecretMinLength, jwtSecret.length())
            );
        }

        log.info("✅ Production configuration validated");
    }
}

