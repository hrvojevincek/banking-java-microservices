package com.bankapp.userservice.service;

import java.util.HashMap;
import java.util.Map;

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

    public CognitoService(@Value("${aws.region}") String region) {
        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();
    }

    public String registerUser(String email, String password, String firstName, String lastName) {
        try {
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

            SignUpRequest signUpRequest = SignUpRequest.builder()
                    .clientId(clientId)
                    .username(email)
                    .password(password)
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
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", email);
            authParams.put("PASSWORD", password);

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
                    .username(email)
                    .confirmationCode(confirmationCode)
                    .build();

            cognitoClient.confirmSignUp(confirmSignUpRequest);
        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Error confirming user signup: " + e.getMessage(), e);
        }
    }
}
