package management.backend.inventory.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user lacks required permissions.
 * Returns 403 Forbidden with a generic message.
 */
public class AuthorizationException extends ApiException {

    public AuthorizationException(String message) {
        super(message, "AUTHORIZATION_ERROR", HttpStatus.FORBIDDEN.value());
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause, "AUTHORIZATION_ERROR", HttpStatus.FORBIDDEN.value());
    }
}
