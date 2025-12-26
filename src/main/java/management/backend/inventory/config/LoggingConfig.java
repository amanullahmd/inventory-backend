package management.backend.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Logging configuration for request/response tracking
 * Adds request ID and timing information to all HTTP requests
 */
@Configuration
public class LoggingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
    }

    /**
     * Interceptor for logging HTTP requests and responses
     */
    public static class LoggingInterceptor implements HandlerInterceptor {

        private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
        private static final String REQUEST_ID = "requestId";
        private static final String START_TIME = "startTime";

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String requestId = java.util.UUID.randomUUID().toString();
            MDC.put(REQUEST_ID, requestId);
            MDC.put(START_TIME, String.valueOf(System.currentTimeMillis()));

            log.info("Incoming request: {} {} from {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());

            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                Object handler, Exception ex) {
            long startTime = Long.parseLong(MDC.get(START_TIME));
            long duration = System.currentTimeMillis() - startTime;

            log.info("Request completed: {} {} - Status: {} - Duration: {}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);

            if (ex != null) {
                log.error("Request failed with exception", ex);
            }

            MDC.clear();
        }
    }
}
