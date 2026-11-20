package com.eldercare.modules.auth.controller;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.auth.dto.request.LoginRequest;
import com.eldercare.modules.auth.dto.response.LoginResponse;
import com.eldercare.modules.security.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping({"/auth", "/api/v1/auth"})
public class AuthController {

    @Autowired
    private com.eldercare.modules.auth.service.AuthService authService;

    @Autowired
    private UserRepository userRepository;

    public record CurrentUser(Long id, String email, String firstName, String lastName,
                              String roleName) {}

    @GetMapping("/me")
    public ResponseEntity<CurrentUser> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserEntity user = userRepository.findActiveByEmailWithRole(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return ResponseEntity.ok(new CurrentUser(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getRole().getRoleName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (com.eldercare.exception.custom.BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (com.eldercare.exception.custom.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            authService.resetPassword(email);
            return ResponseEntity.ok(Map.of("message", "Password reset link sent to email"));
        } catch (com.eldercare.exception.custom.BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        } catch (com.eldercare.exception.custom.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred"));
        }
    }
}
