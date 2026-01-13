package management.backend.inventory.controller;

import management.backend.inventory.dto.*;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.UserRoleEnum;
import management.backend.inventory.service.AuthService;
import management.backend.inventory.service.NativeUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NativeAuthController
 * Tests authentication endpoints
 */
@ExtendWith(MockitoExtension.class)
class NativeAuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private NativeUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NativeAuthController authController;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRoleEnum.USER);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        loginResponse = LoginResponse.builder()
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .email("test@example.com")
            .roles(List.of("ROLE_USER"))
            .build();
    }

    @Test
    @DisplayName("Register creates new user successfully")
    void register_CreatesUser_Successfully() {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(testUser);

        // Act
        ResponseEntity<AuthUserResponse> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertTrue(response.getBody().getRoles().contains("ROLE_USER"));
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Login returns tokens for valid credentials")
    void login_ReturnsTokens_ForValidCredentials() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("access-token", response.getBody().getAccessToken());
        assertEquals("refresh-token", response.getBody().getRefreshToken());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Refresh returns new tokens for valid refresh token")
    void refresh_ReturnsNewTokens_ForValidRefreshToken() {
        // Arrange
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("valid-refresh-token");
        
        LoginResponse refreshedResponse = LoginResponse.builder()
            .accessToken("new-access-token")
            .refreshToken("new-refresh-token")
            .build();
        
        when(authService.refreshAccessToken(any(RefreshRequest.class))).thenReturn(refreshedResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.refresh(refreshRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new-access-token", response.getBody().getAccessToken());
        verify(authService).refreshAccessToken(any(RefreshRequest.class));
    }

    @Test
    @DisplayName("Change password succeeds for authenticated user")
    void changePassword_Succeeds_ForAuthenticatedUser() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword123!");
        
        doNothing().when(authService).changePassword(eq(1L), any(ChangePasswordRequest.class));

        // Act
        ResponseEntity<Void> response = authController.changePassword(authentication, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authService).changePassword(eq(1L), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("Change password returns unauthorized when not authenticated")
    void changePassword_ReturnsUnauthorized_WhenNotAuthenticated() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();

        // Act
        ResponseEntity<Void> response = authController.changePassword(null, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, never()).changePassword(anyLong(), any(ChangePasswordRequest.class));
    }

    @Test
    @DisplayName("Verify password change required returns status")
    void verifyPasswordChangeRequired_ReturnsStatus() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        when(authService.isPasswordChangeRequired(1L)).thenReturn(true);

        // Act
        ResponseEntity<PasswordChangeRequiredResponse> response = 
            authController.verifyPasswordChangeRequired(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getPasswordChangeRequired());
        verify(authService).isPasswordChangeRequired(1L);
    }

    @Test
    @DisplayName("Verify password change required returns unauthorized when not authenticated")
    void verifyPasswordChangeRequired_ReturnsUnauthorized_WhenNotAuthenticated() {
        // Act
        ResponseEntity<PasswordChangeRequiredResponse> response = 
            authController.verifyPasswordChangeRequired(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Get current user returns user info")
    void getCurrentUser_ReturnsUserInfo() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(1L);
        when(userService.getUserWithRole(1L)).thenReturn(testUser);

        // Act
        ResponseEntity<AuthUserResponse> response = authController.getCurrentUser(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(userService).getUserWithRole(1L);
    }

    @Test
    @DisplayName("Get current user returns unauthorized when not authenticated")
    void getCurrentUser_ReturnsUnauthorized_WhenNotAuthenticated() {
        // Act
        ResponseEntity<AuthUserResponse> response = authController.getCurrentUser(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
