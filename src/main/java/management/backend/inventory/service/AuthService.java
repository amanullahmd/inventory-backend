package management.backend.inventory.service;

import lombok.RequiredArgsConstructor;
import management.backend.inventory.config.JwtTokenProvider;
import management.backend.inventory.dto.ChangePasswordRequest;
import management.backend.inventory.dto.LoginRequest;
import management.backend.inventory.dto.LoginResponse;
import management.backend.inventory.dto.RefreshRequest;
import management.backend.inventory.dto.RegisterRequest;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.UserRoleEnum;
import management.backend.inventory.exception.ValidationException;
import management.backend.inventory.repository.GradeRepository;
import management.backend.inventory.repository.UserRepository;
import management.backend.inventory.util.PasswordValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Email already registered");
        }

        if (!passwordValidator.isValid(request.getPassword())) {
            throw new ValidationException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, and numbers");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getEmail());
        user.setName(request.getName() != null ? request.getName() : "");
        user.setEnabled(true);
        user.setPasswordChangeRequired(false);
        user.setTemporaryPassword(false);
        user.setLastPasswordChangeAt(LocalDateTime.now());
        user.setRole(UserRoleEnum.USER);

        if (request.getGradeId() != null) {
            gradeRepository.findById(request.getGradeId())
                    .ifPresent(user::setGrade);
        }

        user = userRepository.save(user);

        log.info("User registered successfully: {}", user.getEmail());
        return user;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new ValidationException("Invalid email or password");
        }

        User user = userOpt.get();
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(List.of(user.getRole().getAuthority()))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900L)
                .tokenType("Bearer")
                .passwordChangeRequired(user.getPasswordChangeRequired())
                .build();
    }

    public LoginResponse refreshAccessToken(RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new ValidationException("Invalid or expired refresh token");
        }

        Long userId = jwtTokenProvider.extractUserIdFromToken(request.getRefreshToken());
        if (userId == null) {
            throw new ValidationException("Invalid token: user ID is missing");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(List.of(user.getRole().getAuthority()))
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .expiresIn(900L)
                .tokenType("Bearer")
                .passwordChangeRequired(user.getPasswordChangeRequired())
                .build();
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("New password and confirmation do not match");
        }

        if (!passwordValidator.isValid(request.getNewPassword())) {
            throw new ValidationException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, and numbers");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ValidationException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangeRequired(false);
        user.setTemporaryPassword(false);
        user.setLastPasswordChangeAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getEmail());
    }

    public boolean isPasswordChangeRequired(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));
        return user.getPasswordChangeRequired() != null && user.getPasswordChangeRequired();
    }

    public boolean validateCredentials(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword());
    }
}
