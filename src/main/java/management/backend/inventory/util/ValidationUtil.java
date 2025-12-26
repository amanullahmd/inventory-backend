package management.backend.inventory.util;

import management.backend.inventory.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern SKU_PATTERN = 
        Pattern.compile("^[A-Z0-9-]{3,50}$");

    /**
     * Validates email format
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty", "email", "Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format", "email", "Email format is invalid");
        }
    }

    /**
     * Validates that a string is not empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty", fieldName, fieldName + " is required");
        }
    }

    /**
     * Validates that a quantity is positive
     */
    public static void validatePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be positive", "quantity", "Quantity must be greater than 0");
        }
    }

    /**
     * Validates SKU format (alphanumeric and hyphens, 3-50 chars)
     */
    public static void validateSku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new ValidationException("SKU cannot be empty", "sku", "SKU is required");
        }
        if (!SKU_PATTERN.matcher(sku).matches()) {
            throw new ValidationException(
                "SKU must be 3-50 characters, uppercase letters, numbers, and hyphens only",
                "sku",
                "SKU format is invalid"
            );
        }
    }

    /**
     * Validates that a value is not null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null", fieldName, fieldName + " is required");
        }
    }

    /**
     * Validates that a numeric value is within range
     */
    public static void validateRange(double value, double min, double max, String fieldName) {
        if (value < min || value > max) {
            throw new ValidationException(
                fieldName + " must be between " + min + " and " + max,
                fieldName,
                "Value is out of range"
            );
        }
    }
}

