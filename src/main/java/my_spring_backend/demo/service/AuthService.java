package my_spring_backend.demo.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import my_spring_backend.demo.config.JwtUtil;
import my_spring_backend.demo.dto.ApiResponse;
import my_spring_backend.demo.dto.ProfileUpdateRequest;
import my_spring_backend.demo.dto.ResetPasswordRequest;
import my_spring_backend.demo.dto.TokenResponse;
import my_spring_backend.demo.exception.UserAlreadyExistsException;
import my_spring_backend.demo.model.PasswordResetToken;
import my_spring_backend.demo.model.User;
import my_spring_backend.demo.repository.PasswordResetTokenRepository;
import my_spring_backend.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final AtomicLong counter = new AtomicLong(0); // auto increment id
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;   

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordResetTokenRepository passwordResetTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;

        // if db already has users, set counter to max existing id
        userRepository.findAll().stream()
                .mapToLong(User::getId)
                .max()
                .ifPresent(counter::set);
    }

    public ResponseEntity<?> register(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty())
            throw new IllegalArgumentException("Please add name!");
        if (user.getMobileNo() == null || user.getMobileNo().trim().isEmpty())
            throw new IllegalArgumentException("Please add mobile number!");
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserAlreadyExistsException("Email already exists!");
        // set autoincrement id
        user.setId(counter.incrementAndGet());
        userRepository.save(user);

        String userId = String.valueOf(user.getId());
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    public ResponseEntity<?> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            String userId = String.valueOf(user.get().getId());
            String accessToken = jwtUtil.generateAccessToken(userId);
            String refreshToken = jwtUtil.generateRefreshToken(userId);

            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        }
        throw new RuntimeException("Invalid username/email or password");
    }

    public ResponseEntity<TokenResponse> refreshToken(String refreshToken) {
        try {
            if (jwtUtil.validateToken(refreshToken)) {
                String userId = jwtUtil.extractUserId(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(userId);

                return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse(null, "Refresh token expired, please login again"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponse(null, "Invalid refresh token"));
        }
    }

    public ResponseEntity<?> createTokenForForgotPassword(PasswordResetToken data) {
        Optional<User> user = userRepository.findByEmail(data.getEmail());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found for " + data.getEmail() + " email");
        }
        // Save token in DB
        PasswordResetToken savedToken = passwordResetTokenRepository.save(data);
        // Send email with token link
        emailService.sendResetPasswordEmail(data.getEmail(), savedToken.getId());

        return ResponseEntity.ok("Password reset email sent successfully!");
    }

    public ResponseEntity<?> resetPassword(String token, ResetPasswordRequest request) {
        // 1. Validate token
        Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findById(token);
        if (resetToken.isEmpty()) {
            throw new RuntimeException("Invalid or expired token");
        }

        // 2. Fetch user by email
        Optional<User> userOpt = userRepository.findByEmail(resetToken.get().getEmail());
        if (userOpt.isEmpty()) {    // this will nearly not happend
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>(200, "Password reset successful", null));
    }

    public ResponseEntity<TokenResponse> loginWithGoogle(String credential) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(googleClientId)) // ✅ injected value
                    .build();

            GoogleIdToken idToken = verifier.verify(credential);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleId = payload.getSubject();

            // check if user exists
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // first time user → create account
                user = new User();
                user.setEmail(email);
                user.setName(name);
                userRepository.save(user);
            }

            // issue JWT
            String userId = String.valueOf(user.getId());
            return ResponseEntity.ok(new TokenResponse(jwtUtil.generateAccessToken(userId), jwtUtil.generateRefreshToken(userId)));

        } catch (Exception e) {
            throw new RuntimeException("Google login failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> updateProfile(String token, ProfileUpdateRequest request) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(401, "Invalid token", null));
            }

            String userId = jwtUtil.extractUserId(token);

            return userRepository.findById(Long.parseLong(userId))
                    .map(user -> {
                        // Update fields if provided
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            user.setName(request.getName().trim());
                        }
                        if (request.getMobileNo() != null && !request.getMobileNo().trim().isEmpty()) {
                            user.setMobileNo(request.getMobileNo().trim());
                        }

                        User updatedUser = userRepository.save(user);
                        return ResponseEntity.ok(new ApiResponse<>(200, "Profile updated successfully", updatedUser));
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(404, "User not found", null)));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("token expired, please login again");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error updating profile", null));
        }
    }

}
