import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        String[] passwords = {
            "Admin@123456",
            "Manager@123456",
            "User@123456"
        };
        
        String[] emails = {
            "admin@example.com",
            "manager@example.com",
            "user@example.com"
        };
        
        for (int i = 0; i < passwords.length; i++) {
            String hash = encoder.encode(passwords[i]);
            System.out.println("-- " + emails[i] + ": " + passwords[i] + " -> " + hash);
        }
    }
}
