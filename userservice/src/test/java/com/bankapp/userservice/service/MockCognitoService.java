package com.bankapp.userservice.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

/**
 * A mock implementation of CognitoService for testing purposes.
 * This avoids making actual AWS Cognito API calls.
 * 
 * NOTE: This service is only activated in the "dev" or "test" profiles
 * and should never be used in production.
 */
@Service
@Primary
@Profile({ "dev", "test" }) // Use this service in dev and test profiles
public class MockCognitoService extends CognitoService {

    // Store registered users in memory for testing - map email to password
    private Map<String, String> registeredUsers = new HashMap<>();

    public MockCognitoService(@Value("${aws.region}") String region) {
        super(region); // Use configured region
        // MockCognitoService doesn't actually use the clientSecret since it doesn't
        // call real Cognito APIs
    }

    @Override
    public String registerUser(String email, String password, String firstName, String lastName) {
        // Generate a mock Cognito user ID
        String cognitoUserId = UUID.randomUUID().toString();

        // Store user credentials for login simulation - using email as the key
        registeredUsers.put(email, password);

        System.out.println("MOCK COGNITO: Registered user " + email + " with ID " + cognitoUserId);

        return cognitoUserId;
    }

    @Override
    public AuthenticationResultType authenticateUser(String email, String password) {
        // Check if user exists and password matches
        if (!registeredUsers.containsKey(email) || !registeredUsers.get(email).equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        // Create mock tokens
        String mockAccessToken = "mock-access-token-" + UUID.randomUUID();
        String mockIdToken = "mock-id-token-" + UUID.randomUUID();
        String mockRefreshToken = "mock-refresh-token-" + UUID.randomUUID();

        System.out.println("MOCK COGNITO: Authenticated user " + email);

        // Build and return mock authentication result
        return AuthenticationResultType.builder()
                .accessToken(mockAccessToken)
                .idToken(mockIdToken)
                .refreshToken(mockRefreshToken)
                .expiresIn(3600)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void confirmSignUp(String email, String confirmationCode) {
        // No-op for mock implementation
        System.out.println("MOCK COGNITO: Confirmed signup for user " + email);
    }
}