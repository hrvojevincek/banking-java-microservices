package com.bankapp.userservice.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

@Service
public class CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    @Value("${aws.cognito.clientSecret}")
    private String clientSecret;

    public CognitoService(@Value("${aws.region}") String region) {
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * Calculate the SECRET_HASH required for Cognito API calls when a client has a
     * secret
     * 
     * @param username The username/email of the user
     * @return The calculated SECRET_HASH value
     */
    private String calculateSecretHash(String username) {
        try {
            // Initialize HMAC-SHA256 with the client secret as the key
            final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
            SecretKeySpec signingKey = new SecretKeySpec(
                    clientSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA256_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);

            // Message to sign is username concatenated with client ID
            mac.update(username.getBytes(StandardCharsets.UTF_8));
            mac.update(clientId.getBytes(StandardCharsets.UTF_8));

            // Return Base64-encoded signature
            byte[] rawHmac = mac.doFinal();
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error calculating SECRET_HASH: " + e.getMessage(), e);
        }
    }

    public String registerUser(String email, String password, String firstName, String lastName) {
        try {
            // Generate a unique username - don't use email as username
            String username = "user-" + UUID.randomUUID().toString().substring(0, 8);

            // Set email as an attribute
            AttributeType emailAttr = AttributeType.builder()
                    .name("email")
                    .value(email)
                    .build();

            AttributeType nameAttr = AttributeType.builder()
                    .name("name")
                    .value(firstName + " " + lastName)
                    .build();

            AttributeType givenNameAttr = AttributeType.builder()
                    .name("given_name")
                    .value(firstName)
                    .build();

            AttributeType familyNameAttr = AttributeType.builder()
                    .name("family_name")
                    .value(lastName)
                    .build();

            // Calculate the SECRET_HASH using the generated username
            String secretHash = calculateSecretHash(username);

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(username) // Use the generated username
                    .password(password)
                    .secretHash(secretHash)
                    .userAttributes(emailAttr, nameAttr, givenNameAttr, familyNameAttr)
                    .build();

            SignUpResponse response = cognitoClient.signUp(signUpRequest);
            return response.userSub();
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Error registering user with Cognito: " + e.getMessage(), e);
        }
    }

    public AuthenticationResultType authenticateUser(String email, String password) {
        try {
            // When using email as an alias, we authenticate using the email
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", email);
            authParams.put("PASSWORD", password);
            authParams.put("SECRET_HASH", calculateSecretHash(email)); // Add SECRET_HASH

            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .userPoolId(userPoolId)
                    .authParameters(authParams)
                    .build();

            AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(authRequest);
            return response.authenticationResult();
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

    public void confirmSignUp(String email, String confirmationCode) {
        try {
            ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                    .clientId(clientId)
                    .username(email) // Use email for confirmation
                    .confirmationCode(confirmationCode)
                    .secretHash(calculateSecretHash(email))
                    .build();

            cognitoClient.confirmSignUp(confirmSignUpRequest);
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Error confirming user signup: " + e.getMessage(), e);
        }
    }
}
