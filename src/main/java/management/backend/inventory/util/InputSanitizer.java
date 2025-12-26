package management.backend.inventory.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Input sanitization utility.
 * Sanitizes user input to prevent injection attacks and XSS.
 * 
 * Requirements: 6.1, 6.5, 6.8
 */
@Slf4j
@Component
public class InputSanitizer {

    /**
     * Sanitize string input by trimming whitespace.
     * 
     * @param input the input to sanitize
     * @return sanitized input, or null if input is null
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    /**
     * Escape HTML special characters to prevent XSS attacks.
     * 
     * @param input the input to escape
     * @return escaped input
     */
    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }

        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * Validate string length.
     * 
     * @param input the input to validate
     * @param maxLength the maximum allowed length
     * @return true if input length is valid, false otherwise
     */
    public boolean isValidLength(String input, int maxLength) {
        if (input == null) {
            return true;
        }
        return input.length() <= maxLength;
    }

    /**
     * Validate string is not empty or whitespace only.
     * 
     * @param input the input to validate
     * @return true if input is not empty, false otherwise
     */
    public boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
}
