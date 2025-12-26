package management.backend.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic API response wrapper for all endpoints.
 * Ensures consistent response format across the API.
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private T data;
    private String message;
    private boolean success;
    
    // Constructors
    public ApiResponse() {
        this.success = true;
    }
    
    public ApiResponse(T data) {
        this.data = data;
        this.success = true;
    }
    
    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = true;
    }
    
    public ApiResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }
    
    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
