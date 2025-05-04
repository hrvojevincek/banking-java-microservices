package com.bankapp.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

public class MockCognitoServiceTest {

    private MockCognitoService mockCognitoService;
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "Password123!";
    private final String TEST_FIRST_NAME = "Test";
    private final String TEST_LAST_NAME = "User";

    @BeforeEach
    void setUp() {
        mockCognitoService = new MockCognitoService();
    }

    @Test
    void testRegisterUser() {
        // Register a user
        String cognitoUserId = mockCognitoService.registerUser(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_FIRST_NAME,
                TEST_LAST_NAME);

        // Verify that a Cognito user ID was generated
        assertNotNull(cognitoUserId);

        // Ensure ID is in UUID format (just checking basic characteristics)
        assertEquals(36, cognitoUserId.length());
        assertEquals(5, cognitoUserId.split("-").length);
    }

    @Test
    void testAuthenticateUserSuccessfully() {
        // Register a user first
        mockCognitoService.registerUser(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_FIRST_NAME,
                TEST_LAST_NAME);

        // Attempt to authenticate with correct credentials
        AuthenticationResultType result = mockCognitoService.authenticateUser(
                TEST_EMAIL,
                TEST_PASSWORD);

        // Verify authentication result
        assertNotNull(result);
        assertNotNull(result.accessToken());
        assertNotNull(result.idToken());
        assertNotNull(result.refreshToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(3600, result.expiresIn());
    }

    @Test
    void testAuthenticateUserWithIncorrectPassword() {
        // Register a user first
        mockCognitoService.registerUser(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_FIRST_NAME,
                TEST_LAST_NAME);

        // Attempt to authenticate with incorrect password
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mockCognitoService.authenticateUser(TEST_EMAIL, "WrongPassword123!");
        });

        // Verify error message
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testAuthenticateUserWithNonExistentUser() {
        // Attempt to authenticate with non-existent user
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mockCognitoService.authenticateUser("nonexistent@example.com", TEST_PASSWORD);
        });

        // Verify error message
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testConfirmSignUp() {
        // This is a no-op in the mock implementation, but we should test it doesn't
        // throw exceptions
        mockCognitoService.confirmSignUp(TEST_EMAIL, "123456");
        // No assertions needed as long as it doesn't throw
    }
}