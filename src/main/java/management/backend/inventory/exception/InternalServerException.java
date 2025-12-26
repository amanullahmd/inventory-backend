package management.backend.inventory.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for unexpected server errors.
 * Returns 500 Internal Server Error without exposing stack traces.
 */
public class InternalServerException extends ApiException {

    public InternalServerException(String message) {
        super(message, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
