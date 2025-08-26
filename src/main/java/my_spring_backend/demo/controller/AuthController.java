package my_spring_backend.demo.controller;

import my_spring_backend.demo.dto.ResetPasswordRequest;
import my_spring_backend.demo.dto.ProfileUpdateRequest;
import my_spring_backend.demo.model.PasswordResetToken;
import my_spring_backend.demo.model.User;
import my_spring_backend.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")

@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        return authService.login(request.get("email"), request.get("password"));
    }

    @PostMapping("/forgot-password/token")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetToken data) {
        return authService.createTokenForForgotPassword(data);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(token, request);
    }

    @PostMapping("/google/login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        return authService.loginWithGoogle(request.get("credential"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        return authService.refreshToken(request.get("refreshToken"));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("X-Authorization") String token,
            @RequestBody ProfileUpdateRequest request
    ) {
        return authService.updateProfile(token, request);
    }
}
