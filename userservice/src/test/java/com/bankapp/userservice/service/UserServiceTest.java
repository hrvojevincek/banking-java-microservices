package com.bankapp.userservice.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.bankapp.userservice.dto.AuthResponse;
import com.bankapp.userservice.dto.LoginRequest;
import com.bankapp.userservice.dto.RegisterRequest;
import com.bankapp.userservice.dto.UserResponse;
import com.bankapp.userservice.model.User;
import com.bankapp.userservice.repository.UserRepository;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CognitoService cognitoService;

    @InjectMocks
    private UserService userService;

    private static final String TEST_COGNITO_ID = "test-cognito-id-123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";
    private static final String TEST_ADDRESS = "123 Test St";
    private static final String TEST_CITY = "Test City";
    private static final String TEST_STATE = "TS";
    private static final String TEST_POSTAL_CODE = "12345";
    private static final String TEST_SSN = "123-45-6789";
    private static final LocalDate TEST_DOB = LocalDate.of(1990, 1, 1);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFirstName(TEST_FIRST_NAME);
        registerRequest.setLastName(TEST_LAST_NAME);
        registerRequest.setAddress(TEST_ADDRESS);
        registerRequest.setCity(TEST_CITY);
        registerRequest.setState(TEST_STATE);
        registerRequest.setPostalCode(TEST_POSTAL_CODE);
        registerRequest.setDateOfBirth(TEST_DOB);
        registerRequest.setSsn(TEST_SSN);

        when(cognitoService.registerUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(TEST_COGNITO_ID);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId("test-user-id-1");
            return savedUser;
        });

        // Act
        UserResponse result = userService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_FIRST_NAME, result.getFirstName());
        assertEquals(TEST_LAST_NAME, result.getLastName());
        assertEquals(TEST_ADDRESS, result.getAddress());
        assertEquals(TEST_CITY, result.getCity());
        assertEquals(TEST_STATE, result.getState());

        verify(cognitoService, times(1)).registerUser(
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_FIRST_NAME,
                TEST_LAST_NAME);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoginUser() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        User existingUser = new User();
        existingUser.setId("test-user-id-1");
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setFirstName(TEST_FIRST_NAME);
        existingUser.setLastName(TEST_LAST_NAME);
        existingUser.setCognitoUserId(TEST_COGNITO_ID);
        existingUser.setAddress(TEST_ADDRESS);
        existingUser.setCity(TEST_CITY);
        existingUser.setState(TEST_STATE);
        existingUser.setPostalCode(TEST_POSTAL_CODE);
        existingUser.setDateOfBirth(TEST_DOB);
        existingUser.setSsn(TEST_SSN);

        AuthenticationResultType authResult = AuthenticationResultType.builder()
                .accessToken("test-access-token")
                .idToken("test-id-token")
                .refreshToken("test-refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();

        when(cognitoService.authenticateUser(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(authResult);

        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(existingUser));

        // Act
        AuthResponse result = userService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test-access-token", result.getAccessToken());
        assertEquals("test-id-token", result.getIdToken());
        assertEquals("test-refresh-token", result.getRefreshToken());

        UserResponse userResult = result.getUser();
        assertNotNull(userResult);
        assertEquals(TEST_EMAIL, userResult.getEmail());
        assertEquals(TEST_FIRST_NAME, userResult.getFirstName());
        assertEquals(TEST_LAST_NAME, userResult.getLastName());

        verify(cognitoService, times(1)).authenticateUser(TEST_EMAIL, TEST_PASSWORD);
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    void testGetUserProfile() {
        // Arrange
        String userId = "test-user-id-1";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setFirstName(TEST_FIRST_NAME);
        existingUser.setLastName(TEST_LAST_NAME);
        existingUser.setCognitoUserId(TEST_COGNITO_ID);
        existingUser.setAddress(TEST_ADDRESS);
        existingUser.setCity(TEST_CITY);
        existingUser.setState(TEST_STATE);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        // Act
        UserResponse result = userService.getUserProfile(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(TEST_EMAIL, result.getEmail());
        assertEquals(TEST_FIRST_NAME, result.getFirstName());
        assertEquals(TEST_LAST_NAME, result.getLastName());

        verify(userRepository, times(1)).findById(userId);
    }
}