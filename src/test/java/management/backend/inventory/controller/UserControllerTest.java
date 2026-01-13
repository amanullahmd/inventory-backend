package management.backend.inventory.controller;

import management.backend.inventory.dto.ApiResponse;
import management.backend.inventory.dto.UserProfileRequest;
import management.backend.inventory.dto.UserProfileResponse;
import management.backend.inventory.dto.UserUpdateRequest;
import management.backend.inventory.service.NativeUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController
 * Tests user management endpoints
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {

    @Mock
    private NativeUserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private UserProfileResponse testUserProfile;

    @BeforeEach
    void setUp() {
        testUserProfile = new UserProfileResponse();
        testUserProfile.setId(1L);
        testUserProfile.setName("Test User");
        testUserProfile.setEmail("test@example.com");
        testUserProfile.setBranchName("Main Branch");
        testUserProfile.setRoles(Arrays.asList("ROLE_USER"));
        testUserProfile.setCreatedAt(LocalDateTime.now());
        testUserProfile.setUpdatedAt(LocalDateTime.now());

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Get user profile returns profile for authenticated user")
    void getUserProfile_ReturnsProfile_WhenAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        when(userService.getUserProfile(1L)).thenReturn(testUserProfile);

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.getUserProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals("Test User", response.getBody().getData().getName());
        assertEquals("test@example.com", response.getBody().getData().getEmail());
        verify(userService).getUserProfile(1L);
    }

    @Test
    @DisplayName("Get all users returns list of users")
    void getAllUsers_ReturnsUserList() {
        // Arrange
        List<UserProfileResponse> users = Arrays.asList(testUserProfile);
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserProfileResponse>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test User", response.getBody().get(0).getName());
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Get all users returns empty list when no users")
    void getAllUsers_ReturnsEmptyList_WhenNoUsers() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<UserProfileResponse>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Update user profile returns updated profile")
    void updateUserProfile_ReturnsUpdatedProfile() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");
        
        UserProfileResponse updatedProfile = new UserProfileResponse();
        updatedProfile.setId(1L);
        updatedProfile.setName("Updated Name");
        updatedProfile.setEmail("updated@example.com");
        
        when(userService.updateUserProfile(eq(1L), any(UserProfileRequest.class))).thenReturn(updatedProfile);

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.updateUserProfile(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getData().getName());
        verify(userService).updateUserProfile(eq(1L), any(UserProfileRequest.class));
    }

    @Test
    @DisplayName("Update user profile returns conflict when email exists")
    void updateUserProfile_ReturnsConflict_WhenEmailExists() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        
        UserProfileRequest request = new UserProfileRequest();
        request.setEmail("existing@example.com");
        
        when(userService.updateUserProfile(eq(1L), any(UserProfileRequest.class)))
            .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.updateUserProfile(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Update user by admin returns updated user")
    void updateUser_ReturnsUpdatedUser_WhenAdmin() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@example.com");
        
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(testUserProfile);

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @DisplayName("Update user returns conflict when email exists")
    void updateUser_ReturnsConflict_WhenEmailExists() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("existing@example.com");
        
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
            .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("Update user returns bad request for invalid data")
    void updateUser_ReturnsBadRequest_WhenInvalidData() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class)))
            .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act
        ResponseEntity<ApiResponse<UserProfileResponse>> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
