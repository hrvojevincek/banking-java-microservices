package com.bankapp.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        mockCognitoService = new MockCognitoService("eu-central-1");
    }

    @Test
    void testRegisterUser() {
        // Register a user
        String result = mockCognitoService.registerUser(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_FIRST_NAME,
                TEST_LAST_NAME);

        // Verify that a result was generated
        assertNotNull(result);

        // Split the result into cognitoUserId and username
        String[] parts = result.split(":");
        assertEquals(2, parts.length);

        String cognitoUserId = parts[0];
        String username = parts[1];

        // Verify cognitoUserId is in UUID format
        assertEquals(36, cognitoUserId.length());
        assertEquals(5, cognitoUserId.split("-").length);

        // Verify username has expected format
        assertTrue(username.startsWith("user-"));
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