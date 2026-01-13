package management.backend.inventory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Application configuration properties.
 * Externalized configuration for different environments.
 * 
 * Requirements: 7.1, 7.2, 7.3, 7.4
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Cors cors = new Cors();
    private Security security = new Security();

    @Data
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>(Arrays.asList("http://localhost:3000", "http://localhost:3001"));
        
        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
        
        public void setAllowedOrigins(String allowedOrigins) {
            if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                this.allowedOrigins = Arrays.asList(allowedOrigins.split(","));
            }
        }
    }

    @Data
    public static class Security {
        private int jwtSecretMinLength = 32;
    }
}
