package com.bankapp.userservice.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankapp.userservice.dto.AuthResponse;
import com.bankapp.userservice.dto.LoginRequest;
import com.bankapp.userservice.dto.RegisterRequest;
import com.bankapp.userservice.dto.UserResponse;

/**
 * Simple test controller for Postman testing purposes
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    // Simple in-memory storage for testing
    private Map<String, UserResponse> users = new HashMap<>();

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        // Create a mock user response
        UserResponse user = new UserResponse();
        user.setId(UUID.randomUUID().toString());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPostalCode(request.getPostalCode());
        user.setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : LocalDate.of(1990, 1, 1));
        user.setSsn(request.getSsn());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Store user in memory
        users.put(user.getId(), user);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Create mock tokens
        String accessToken = "mock-access-token-" + UUID.randomUUID();
        String idToken = "mock-id-token-" + UUID.randomUUID();
        String refreshToken = "mock-refresh-token-" + UUID.randomUUID();

        // Find a user with matching email (for testing purposes)
        UserResponse matchingUser = users.values().stream()
                .filter(u -> u.getEmail().equals(request.getEmail()))
                .findFirst()
                .orElseGet(() -> {
                    // If no matching user, create a fake one for testing
                    UserResponse user = new UserResponse();
                    user.setId(UUID.randomUUID().toString());
                    user.setEmail(request.getEmail());
                    user.setFirstName("Test");
                    user.setLastName("User");
                    return user;
                });

        // Build response with tokens
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .idToken(idToken)
                .refreshToken(refreshToken)
                .user(matchingUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}