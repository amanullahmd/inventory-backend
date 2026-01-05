package management.backend.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.lang.NonNull;

/**
 * Rate limiting configuration
 * Implements simple rate limiting per IP address
 */
@Configuration
@Profile({"prod", "staging"})
public class RateLimitingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor());
    }

    /**
     * Interceptor for rate limiting
     */
    public static class RateLimitingInterceptor implements HandlerInterceptor {

        private static final int REQUESTS_PER_MINUTE = 100;
        private static final long WINDOW_SIZE_MS = 60_000; // 1 minute
        private static final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

        private static class RequestCounter {
            AtomicInteger count = new AtomicInteger(0);
            long windowStart = System.currentTimeMillis();

            synchronized boolean isAllowed() {
                long now = System.currentTimeMillis();
                if (now - windowStart > WINDOW_SIZE_MS) {
                    // Reset window
                    count.set(0);
                    windowStart = now;
                }
                return count.incrementAndGet() <= REQUESTS_PER_MINUTE;
            }
        }

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) 
                throws Exception {
            String clientIp = getClientIp(request);
            RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());

            if (!counter.isAllowed()) {
                response.setStatus(429); // 429 Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Rate limit exceeded. Maximum 100 requests per minute.\"}");
                return false;
            }

            return true;
        }

        private String getClientIp(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
    }
}
