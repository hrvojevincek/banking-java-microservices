package com.bankapp.userservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bankapp.userservice.dto.TokenExchangeRequest;
import com.bankapp.userservice.dto.TokenExchangeResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RestTemplate restTemplate;

    @Value("${aws.cognito.domain}")
    private String cognitoDomain;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    // Use a default empty value if the property is not found
    @Value("${aws.cognito.clientSecret:}")
    private String clientSecret;

    public AuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenExchangeResponse> exchangeToken(@RequestBody TokenExchangeRequest request) {
        // Set up headers for token exchange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set up form data for token exchange
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isEmpty()) {
            formData.add("client_secret", clientSecret);
        }
        formData.add("code", request.getCode());
        formData.add("redirect_uri", request.getRedirectUri());

        // Create HTTP entity with headers and form data
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // Make request to Cognito token endpoint
        ResponseEntity<TokenExchangeResponse> response = restTemplate.postForEntity(
                cognitoDomain + "/oauth2/token",
                requestEntity,
                TokenExchangeResponse.class);

        return response;
    }
}