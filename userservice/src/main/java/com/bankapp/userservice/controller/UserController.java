package com.bankapp.userservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankapp.userservice.dto.AuthResponse;
import com.bankapp.userservice.dto.LoginRequest;
import com.bankapp.userservice.dto.RegisterRequest;
import com.bankapp.userservice.dto.UserResponse;
import com.bankapp.userservice.dto.VerifyEmailRequest;
import com.bankapp.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        userService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unverified/{email}")
    public ResponseEntity<Void> deleteUnverifiedUser(@PathVariable String email) {
        boolean deleted = userService.deleteUnverifiedUser(email);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            // Try to get email from token claims
            String email = jwt.getClaimAsString("email");
            UserResponse user = null;

            // First try to find user by email
            if (email != null) {
                user = userService.getUserByEmail(email);
            }

            // If not found by email, try cognito username
            if (user == null) {
                String cognitoUsername = jwt.getClaimAsString("cognito:username");
                if (cognitoUsername != null) {
                    user = userService.getUserByCognitoUsername(cognitoUsername);
                }
            }

            // If still not found, try sub as cognito user ID
            if (user == null) {
                String cognitoUserId = jwt.getClaimAsString("sub");
                if (cognitoUserId != null) {
                    user = userService.getUserByCognitoUserId(cognitoUserId);
                }
            }

            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
