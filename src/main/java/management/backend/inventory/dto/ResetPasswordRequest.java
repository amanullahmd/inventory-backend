package management.backend.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for password reset requests.
 * Requirements: 5.3 - Password reset via Keycloak Admin API
 */
public class ResetPasswordRequest {
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
    
    // Default constructor
    public ResetPasswordRequest() {}
    
    // Constructor
    public ResetPasswordRequest(String newPassword) {
        this.newPassword = newPassword;
    }
    
    // Getters and setters
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    @Override
    public String toString() {
        return "ResetPasswordRequest{" +
                "newPassword='[PROTECTED]'" +
                '}';
    }
}