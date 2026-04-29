package io.waldopo.schoolsecurity.auth;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public Map<String, String> token(@RequestBody LoginRequest request) {
        String role = switch (request.username()) {
            case "principal" -> "ROLE_PRINCIPAL";
            case "teacher" -> "ROLE_TEACHER";
            default -> "ROLE_PARENT";
        };
        return Map.of("token", jwtService.generate(request.username(), role));
    }

    public record LoginRequest(@NotBlank String username) {}
}
