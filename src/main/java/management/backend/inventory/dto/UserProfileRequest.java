package management.backend.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user profile update requests.
 * Requirements: 1.1, 1.2, 1.3, 1.4
 */
public class UserProfileRequest {
    
    @NotBlank(message = "Name is required and cannot be blank")
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required and cannot be blank")
    private String email;
    
    private String branchName;
    private String position;
    private String grade;
    
    // Constructors
    public UserProfileRequest() {}
    
    public UserProfileRequest(String name, String email, String branchName) {
        this.name = name;
        this.email = email;
        this.branchName = branchName;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    @Override
    public String toString() {
        return "UserProfileRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", branchName='" + branchName + '\'' +
                '}';
    }
}
