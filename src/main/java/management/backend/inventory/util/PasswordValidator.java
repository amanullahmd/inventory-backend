package management.backend.inventory.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Password validation utility.
 * Validates password strength according to security requirements.
 * 
 * Requirements: 4.9, 6.3, 6.4
 */
@Slf4j
@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");

    /**
     * Validate password strength.
     * Password must be at least 8 characters and contain uppercase, lowercase, and numbers.
     * 
     * @param password the password to validate
     * @return true if password is valid, false otherwise
     */
    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        if (password.length() < MIN_LENGTH) {
            return false;
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return false;
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return false;
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            return false;
        }

        return true;
    }

    /**
     * Validate password and return detailed error message.
     * 
     * @param password the password to validate
     * @return error message if invalid, null if valid
     */
    public String getValidationError(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < MIN_LENGTH) {
            return String.format("Password must be at least %d characters long", MIN_LENGTH);
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return "Password must contain at least one uppercase letter";
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return "Password must contain at least one lowercase letter";
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            return "Password must contain at least one digit";
        }

        return null;
    }

    /**
     * Get password strength requirements as a string.
     * 
     * @return password requirements
     */
    public String getRequirements() {
        return String.format(
                "Password must be at least %d characters and contain uppercase, lowercase, and numbers",
                MIN_LENGTH
        );
    }
}
