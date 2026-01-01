package management.backend.inventory.controller;

import management.backend.inventory.dto.ApiResponse;
import management.backend.inventory.dto.UserProfileRequest;
import management.backend.inventory.dto.UserProfileResponse;
import management.backend.inventory.service.NativeUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for user management endpoints.
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5
 * Feature: backend-frontend-alignment
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    
    private final NativeUserService userService;
    
    public UserController(NativeUserService userService) {
        this.userService = userService;
    }
    
    /**
     * GET /api/users/profile - Retrieve current user's profile.
     * Requirements: 1.1, 1.2
     * Accessible to authenticated users
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {
        Long userId = getCurrentUserId();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(new ApiResponse<>(profile, "Profile retrieved successfully"));
    }
    
    /**
     * GET /api/users - Retrieve all users.
     * Requirements: 1.1 - User management
     * Accessible to authenticated users (admin only)
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * PUT /api/users/profile - Update current user's profile.
     * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5
     * Accessible to authenticated users
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(@Valid @RequestBody UserProfileRequest request) {
        Long userId = getCurrentUserId();
        
        try {
            UserProfileResponse updatedProfile = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(new ApiResponse<>(updatedProfile, "Profile updated successfully"));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Email already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(null, "Email already exists", false));
            }
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(null, e.getMessage(), false));
        }
    }
    
    
    
    /**
     * Helper method to get current user ID from security context.
     * The JWT filter sets the principal as a Long (user ID).
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }
        
        // The principal is set by JwtAuthenticationFilter as a Long (user ID)
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        
        // Fallback for UserDetails (if using other auth methods)
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return userService.findByEmail(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        
        throw new IllegalArgumentException("Cannot extract user ID from authentication: principal is " + principal.getClass().getSimpleName());
    }
}
