package com.bankapp.userservice.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bankapp.userservice.dto.AuthResponse;
import com.bankapp.userservice.dto.LoginRequest;
import com.bankapp.userservice.dto.RegisterRequest;
import com.bankapp.userservice.dto.UserResponse;
import com.bankapp.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private RegisterRequest registerRequest;
    private UserResponse userResponse;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Setup test data for registration
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("test.user@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setAddress("123 Test St");
        registerRequest.setCity("Test City");
        registerRequest.setState("TS");
        registerRequest.setPostalCode("12345");
        registerRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registerRequest.setSsn("123-45-6789");

        // Setup mock user response
        userResponse = new UserResponse();
        userResponse.setId("test-user-id-123");
        userResponse.setFirstName("Test");
        userResponse.setLastName("User");
        userResponse.setEmail("test.user@example.com");
        userResponse.setAddress("123 Test St");
        userResponse.setCity("Test City");
        userResponse.setState("TS");
        userResponse.setPostalCode("12345");
        userResponse.setDateOfBirth(LocalDate.of(1990, 1, 1));
        userResponse.setSsn("123-45-6789");
        userResponse.setCreatedAt(LocalDateTime.now());
        userResponse.setUpdatedAt(LocalDateTime.now());

        // Setup test data for login
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test.user@example.com");
        loginRequest.setPassword("Password123!");

        // Setup mock auth response
        authResponse = AuthResponse.builder()
                .accessToken("mock-access-token")
                .idToken("mock-id-token")
                .refreshToken("mock-refresh-token")
                .user(userResponse)
                .build();
    }

    @Test
    void testRegisterUser() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-user-id-123"))
                .andExpect(jsonPath("$.email").value("test.user@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void testLoginUser() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.idToken").value("mock-id-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
                .andExpect(jsonPath("$.user.id").value("test-user-id-123"))
                .andExpect(jsonPath("$.user.email").value("test.user@example.com"));
    }
}