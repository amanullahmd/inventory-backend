package management.backend.inventory.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails.
 * Returns 401 Unauthorized without exposing sensitive information.
 */
public class AuthenticationException extends ApiException {

    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR", HttpStatus.UNAUTHORIZED.value());
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, "AUTHENTICATION_ERROR", HttpStatus.UNAUTHORIZED.value());
    }
}
