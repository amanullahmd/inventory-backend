package management.backend.inventory.util;

import org.slf4j.Logger;

/**
 * Utility class for consistent logging patterns
 */
public class LoggingUtil {

    /**
     * Log method entry with parameters
     */
    public static void logMethodEntry(Logger logger, String methodName, Object... params) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Entering method: ").append(methodName);
            if (params.length > 0) {
                sb.append(" with parameters: ");
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(params[i]);
                }
            }
            logger.debug(sb.toString());
        }
    }

    /**
     * Log method exit with result
     */
    public static void logMethodExit(Logger logger, String methodName, Object result) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting method: {} with result: {}", methodName, result);
        }
    }

    /**
     * Log method exception
     */
    public static void logMethodException(Logger logger, String methodName, Exception ex) {
        logger.error("Exception in method: {}", methodName, ex);
    }

    /**
     * Log business operation
     */
    public static void logOperation(Logger logger, String operation, String details) {
        logger.info("Operation: {} - {}", operation, details);
    }

    /**
     * Log security event
     */
    public static void logSecurityEvent(Logger logger, String event, String details) {
        logger.warn("Security event: {} - {}", event, details);
    }

    /**
     * Log performance metric
     */
    public static void logPerformance(Logger logger, String operation, long durationMs) {
        if (durationMs > 1000) {
            logger.warn("Slow operation: {} took {}ms", operation, durationMs);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Operation: {} took {}ms", operation, durationMs);
        }
    }
}
