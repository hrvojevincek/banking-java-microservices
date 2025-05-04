package com.bankapp.userservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankapp.userservice.dto.AuthResponse;
import com.bankapp.userservice.dto.LoginRequest;
import com.bankapp.userservice.dto.RegisterRequest;
import com.bankapp.userservice.dto.UserResponse;
import com.bankapp.userservice.model.User;
import com.bankapp.userservice.repository.UserRepository;

import jakarta.validation.Valid;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

@Service
public class UserService {

    private final UserRepository repository;
    private final CognitoService cognitoService;

    @Autowired
    public UserService(UserRepository repository, CognitoService cognitoService) {
        this.repository = repository;
        this.cognitoService = cognitoService;
    }

    public UserResponse register(@Valid RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Register with Cognito first
        String cognitoResponse = cognitoService.registerUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName());

        // Parse the response which now includes both userId and username
        String[] cognitoParts = cognitoResponse.split(":");
        String cognitoUserId = cognitoParts[0];
        String cognitoUsername = cognitoParts[1];

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPostalCode(request.getPostalCode());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setSsn(request.getSsn());

        // Set Cognito fields
        user.setCognitoUserId(cognitoUserId);
        user.setCognitoUsername(cognitoUsername);
        user.setEmailVerified(false);
        user.setCognitoUserStatus("UNCONFIRMED");

        User savedUser = repository.save(user);
        return mapUserToResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate with Cognito
        AuthenticationResultType authResult = cognitoService.authenticateUser(
                request.getEmail(),
                request.getPassword());

        // Get user from database
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
                .idToken(authResult.idToken())
                .accessToken(authResult.accessToken())
                .refreshToken(authResult.refreshToken())
                .user(mapUserToResponse(user))
                .build();
    }

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return mapUserToResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapUserToResponse(user);
    }

    public UserResponse getUserByCognitoUsername(String cognitoUsername) {
        User user = repository.findByCognitoUsername(cognitoUsername)
                .orElseThrow(() -> new RuntimeException("User not found with cognitoUsername: " + cognitoUsername));

        return mapUserToResponse(user);
    }

    public UserResponse getUserByCognitoUserId(String cognitoUserId) {
        User user = repository.findByCognitoUserId(cognitoUserId)
                .orElseThrow(() -> new RuntimeException("User not found with cognitoUserId: " + cognitoUserId));

        return mapUserToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    public void verifyEmail(String email, String code) {
        try {
            // Get user to retrieve Cognito username
            User user = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Call Cognito to confirm signup with the stored username
            cognitoService.confirmSignUp(user.getCognitoUsername(), code);

            // Update user status in our database
            user.setEmailVerified(true);
            user.setCognitoUserStatus("CONFIRMED");
            repository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify email: " + e.getMessage(), e);
        }
    }

    public boolean deleteUnverifiedUser(String email) {
        return repository.findByEmail(email)
                .filter(user -> !Boolean.TRUE.equals(user.getEmailVerified()))
                .map(user -> {
                    repository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    public int cleanupUnverifiedUsers(LocalDateTime olderThan) {
        List<User> unverifiedUsers = repository.findByEmailVerifiedFalseAndCreatedAtBefore(olderThan);
        int count = unverifiedUsers.size();
        repository.deleteAll(unverifiedUsers);
        return count;
    }

    private UserResponse mapUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        // Do not include password in response
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setPostalCode(user.getPostalCode());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setSsn(user.getSsn());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
