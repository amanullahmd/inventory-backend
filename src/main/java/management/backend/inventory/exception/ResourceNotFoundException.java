package management.backend.inventory.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * Returns 404 Not Found with resource identifier.
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id %d not found", resourceName, id), "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s with identifier %s not found", resourceName, identifier), "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    public static ResourceNotFoundException itemNotFound(Integer itemId) {
        return new ResourceNotFoundException("Item", itemId.longValue());
    }

    public static ResourceNotFoundException userNotFound(Long userId) {
        return new ResourceNotFoundException("User", userId);
    }

    public static ResourceNotFoundException categoryNotFound(Integer categoryId) {
        return new ResourceNotFoundException("Category", categoryId.longValue());
    }
}
