package management.backend.inventory.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a conflict occurs (e.g., duplicate email).
 * Returns 409 Conflict with conflict details.
 */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(message, "CONFLICT_ERROR", HttpStatus.CONFLICT.value());
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, "CONFLICT_ERROR", HttpStatus.CONFLICT.value());
    }

    public static ConflictException emailAlreadyExists(String email) {
        return new ConflictException(String.format("Email '%s' is already registered", email));
    }

    public static ConflictException skuAlreadyExists(String sku) {
        return new ConflictException(String.format("SKU '%s' already exists", sku));
    }
}
