package management.backend.inventory;

import lombok.RequiredArgsConstructor;
import management.backend.inventory.entity.User;
import management.backend.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import management.backend.inventory.entity.Role;
import management.backend.inventory.repository.RoleRepository;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner initDemoUsers(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		return args -> {
			// Initialize demo users with proper BCrypt encoded passwords
			initializeUser(userRepository, passwordEncoder, roleRepository, "admin@example.com", "Admin@123456", "Administrator");
			initializeUser(userRepository, passwordEncoder, roleRepository, "user@example.com", "User@123456", "Standard User");
		};
	}

	private void initializeUser(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
								String email, String password, String roleName) {
		var existingUser = userRepository.findByEmail(email);
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            System.out.println("Role not found: " + roleName);
            return;
        }
		
        // DEBUG: Print permissions for the role
        System.out.println("Initializing user " + email + " with role " + roleName);
        System.out.println("Permissions for " + roleName + ": " + role.getPermissions().size());
        role.getPermissions().forEach(p -> System.out.println(" - " + p.getName()));

		if (existingUser.isEmpty()) {
			// Create new user
			User user = new User();
			user.setEmail(email);
			user.setPassword(passwordEncoder.encode(password));
			user.setUsername(email);
			user.setRole(role);
			user.setEnabled(true);
			user.setPasswordChangeRequired(false);
			userRepository.save(user);
		} else {
			// Update existing user with new password
			User user = existingUser.get();
			user.setPassword(passwordEncoder.encode(password));
			user.setRole(role);
			userRepository.save(user);
		}
	}

}
