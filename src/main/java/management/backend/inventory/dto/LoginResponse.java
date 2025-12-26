package management.backend.inventory.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String email;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
    private Boolean passwordChangeRequired;
}

