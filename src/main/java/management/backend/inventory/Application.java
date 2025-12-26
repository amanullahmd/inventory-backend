package management.backend.inventory;

import lombok.RequiredArgsConstructor;
import management.backend.inventory.entity.User;
import management.backend.inventory.entity.UserRoleEnum;
import management.backend.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner initDemoUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Initialize demo users with proper BCrypt encoded passwords
			initializeUser(userRepository, passwordEncoder, "admin@example.com", "Admin@123456", UserRoleEnum.ADMIN);
			initializeUser(userRepository, passwordEncoder, "user@example.com", "User@123456", UserRoleEnum.USER);
		};
	}

	private void initializeUser(UserRepository userRepository, PasswordEncoder passwordEncoder, 
								String email, String password, UserRoleEnum role) {
		var existingUser = userRepository.findByEmail(email);
		
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
