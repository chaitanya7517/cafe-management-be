package my_spring_backend.demo.controller;

import my_spring_backend.demo.dto.ResetPasswordRequest;
import my_spring_backend.demo.model.PasswordResetToken;
import my_spring_backend.demo.model.User;
import my_spring_backend.demo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
        return authService.login(request.get("usernameOrEmail"), request.get("password"));
    }

    @PostMapping("/forgot-password/token")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetToken data) {
        return authService.createToken(data);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(token, request);
    }

    @PostMapping("/google/login")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String credential = request.get("credential");
        String token = authService.loginWithGoogle(credential);
        return ResponseEntity.ok(Collections.singletonMap("accessToken", token));
    }


}
