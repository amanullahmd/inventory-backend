package management.backend.inventory.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Email validation utility.
 * Validates email format according to RFC 5322 (simplified).
 * 
 * Requirements: 6.2
 */
@Slf4j
@Component
public class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Validate email format.
     * 
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    public boolean isValid(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        if (email.length() > 255) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate email and return error message.
     * 
     * @param email the email to validate
     * @return error message if invalid, null if valid
     */
    public String getValidationError(String email) {
        if (email == null || email.isEmpty()) {
            return "Email cannot be empty";
        }

        if (email.length() > 255) {
            return "Email is too long (maximum 255 characters)";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Email format is invalid";
        }

        return null;
    }
}
