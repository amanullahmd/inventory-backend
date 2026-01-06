package management.backend.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import management.backend.inventory.dto.*;
import management.backend.inventory.entity.User;
import management.backend.inventory.service.AuthService;
import management.backend.inventory.service.NativeUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class NativeAuthController {
    private static final Logger log = LoggerFactory.getLogger(NativeAuthController.class);
    private final AuthService authService;
    private final NativeUserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthUserResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        User user = authService.register(request);
        AuthUserResponse response = AuthUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(List.of(user.getRole().getName()))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt for email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        log.info("Refreshing access token");
        LoginResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) authentication.getPrincipal();
        log.info("User {} attempting to change password", userId);
        authService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-password-change-required")
    public ResponseEntity<PasswordChangeRequiredResponse> verifyPasswordChangeRequired(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) authentication.getPrincipal();
        boolean required = authService.isPasswordChangeRequired(userId);
        return ResponseEntity.ok(PasswordChangeRequiredResponse.builder()
                .passwordChangeRequired(required)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUserWithRole(userId);

        AuthUserResponse response = AuthUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(List.of(user.getRole().getName()))
                .build();

        return ResponseEntity.ok(response);
    }
}
