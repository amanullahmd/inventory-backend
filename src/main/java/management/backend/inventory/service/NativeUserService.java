package management.backend.inventory.service;

import lombok.RequiredArgsConstructor;
import management.backend.inventory.dto.UserProfileRequest;
import management.backend.inventory.dto.UserProfileResponse;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.Role;
import management.backend.inventory.repository.GradeRepository;
import management.backend.inventory.repository.RoleRepository;
import management.backend.inventory.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NativeUserService {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public User createUser(User user) {
        // Password should already be encoded before calling this method
        // Default role is Standard User if not specified
        if (user.getRole() == null) {
            Role defaultRole = roleRepository.findByName("Standard User")
                    .orElseThrow(() -> new RuntimeException("Default role 'Standard User' not found"));
            user.setRole(defaultRole);
        }
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        if (id == null) return Optional.empty();
        return userRepository.findById(id);
    }

    @Transactional
    public void assignRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        user.setRole(role);
        userRepository.save(user);
    }

    public User getUserWithRole(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Role getUserRole(Long userId) {
        User user = getUserWithRole(userId);
        return user.getRole();
    }

    public boolean isAdmin(Long userId) {
        return "Administrator".equals(getUserRole(userId).getName());
    }

    public boolean isUser(Long userId) {
        return "Standard User".equals(getUserRole(userId).getName());
    }

    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate email uniqueness if email is being changed
        if (!user.getEmail().equals(request.getEmail())) {
            validateEmailUniqueness(request.getEmail(), userId);
        }

        // Update profile fields
        user.setEmail(request.getEmail());
        // Name field is now a single field
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        if (request.getGradeId() != null) {
            gradeRepository.findById(request.getGradeId())
                    .ifPresent(user::setGrade);
        }

        User updatedUser = userRepository.save(user);
        return new UserProfileResponse(updatedUser);
    }

    public UserProfileResponse getUserProfile(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserProfileResponse(user);
    }

    /**
     * Get all users in the system.
     * Returns a list of UserProfileResponse objects for all users.
     */
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserProfileResponse::new)
                .collect(Collectors.toList());
    }

    public void validateEmailUniqueness(String email, Long userId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
