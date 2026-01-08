package management.backend.inventory.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 * 
 * Provides endpoint for monitoring application and database health.
 * Used by Railway and monitoring systems to verify deployment status.
 */
@Slf4j
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Health check endpoint
     * 
     * Returns HTTP 200 with status "UP" if application and database are healthy.
     * Returns HTTP 503 with status "DOWN" if database is unreachable.
     * 
     * @return HealthResponse with status and details
     */
    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        try {
            // Test database connection
            testDatabaseConnection();
            
            long uptime = System.currentTimeMillis();
            HealthResponse response = new HealthResponse(
                "UP",
                uptime,
                "Application and database are healthy",
                buildDetails("UP")
            );
            
            log.info("Health check: UP - Database connected successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Health check: DOWN - Database connection failed", e);
            
            HealthResponse response = new HealthResponse(
                "DOWN",
                System.currentTimeMillis(),
                "Database connection failed: " + e.getMessage(),
                buildDetails("DOWN")
            );
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Test database connection
     * 
     * @throws Exception if connection fails
     */
    private void testDatabaseConnection() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            if (conn == null || conn.isClosed()) {
                throw new Exception("Database connection is closed");
            }
            log.debug("Database connection test successful");
        }
    }

    /**
     * Build health details
     * 
     * @param status Health status
     * @return Map with health details
     */
    private Map<String, Object> buildDetails(String status) {
        Map<String, Object> details = new HashMap<>();
        details.put("status", status);
        details.put("timestamp", System.currentTimeMillis());
        details.put("uptime", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        details.put("database", "PostgreSQL");
        return details;
    }

    /**
     * Health Response DTO
     */
    public static class HealthResponse {
        public String status;
        public long timestamp;
        public String message;
        public Map<String, Object> details;

        public HealthResponse(String status, long timestamp, String message, Map<String, Object> details) {
            this.status = status;
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }

        // Getters for JSON serialization
        public String getStatus() {
            return status;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getDetails() {
            return details;
        }
    }
}

/**
 * Custom Health Indicator for Spring Boot Actuator
 */
@Slf4j
@Component
class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try {
            try (Connection conn = dataSource.getConnection()) {
                if (conn == null || conn.isClosed()) {
                    return Health.down()
                        .withDetail("reason", "Database connection is closed")
                        .build();
                }
            }
            
            log.debug("Database health check: UP");
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "Connected")
                .build();
                
        } catch (Exception e) {
            log.error("Database health check failed", e);
            return Health.down()
                .withDetail("reason", e.getMessage())
                .withException(e)
                .build();
        }
    }
}
