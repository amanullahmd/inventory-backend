package management.backend.inventory.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing and temporary password generation utility.
 * Uses BCrypt for secure password hashing.
 * 
 * Requirements: 4.9, 1.1, 1.7
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TEMP_PASSWORD_LENGTH = 16;

    /**
     * Hash a password using BCrypt.
     * 
     * @param password the plaintext password
     * @return the hashed password
     */
    public String hash(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Verify a plaintext password against a hash.
     * 
     * @param plainPassword the plaintext password
     * @param hashedPassword the hashed password
     * @return true if password matches, false otherwise
     */
    public boolean verify(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Generate a cryptographically secure temporary password.
     * Temporary password meets strength requirements.
     * 
     * @return a temporary password
     */
    public String generateTemporaryPassword() {
        // Generate a random password that meets strength requirements
        // Format: 2 uppercase + 2 lowercase + 2 digits + 10 random alphanumeric
        StringBuilder password = new StringBuilder();

        // Add 2 uppercase letters
        for (int i = 0; i < 2; i++) {
            password.append((char) ('A' + SECURE_RANDOM.nextInt(26)));
        }

        // Add 2 lowercase letters
        for (int i = 0; i < 2; i++) {
            password.append((char) ('a' + SECURE_RANDOM.nextInt(26)));
        }

        // Add 2 digits
        for (int i = 0; i < 2; i++) {
            password.append(SECURE_RANDOM.nextInt(10));
        }

        // Add remaining random alphanumeric characters
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int remainingLength = TEMP_PASSWORD_LENGTH - password.length();
        for (int i = 0; i < remainingLength; i++) {
            password.append(alphanumeric.charAt(SECURE_RANDOM.nextInt(alphanumeric.length())));
        }

        // Shuffle the password
        String result = password.toString();
        char[] chars = result.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        String tempPassword = new String(chars);

        // Verify the generated password meets requirements
        if (!passwordValidator.isValid(tempPassword)) {
            log.warn("Generated temporary password does not meet requirements, retrying");
            return generateTemporaryPassword();
        }

        return tempPassword;
    }

    /**
     * Generate a secure random token (for refresh tokens, etc.).
     * 
     * @return a secure random token
     */
    public String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
