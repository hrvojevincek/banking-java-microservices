package com.bankapp.userservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test utility controller to display configuration values.
 * Useful for debugging and verifying environment configuration.
 * 
 * NOTE: This controller is only activated in the "dev" or "test" profiles
 * and should never be used in production.
 */
@RestController
@RequestMapping("/api/config-test")
@Profile({ "dev", "test" }) // Only activate this controller in dev/test environments
public class ConfigTestController {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.cognito.userPoolId}")
    private String cognitoUserPoolId;

    @GetMapping
    public Map<String, String> getConfigInfo() {
        Map<String, String> config = new HashMap<>();
        config.put("activeProfile", activeProfile);
        config.put("awsRegion", awsRegion);
        config.put("cognitoUserPoolId", cognitoUserPoolId);
        return config;
    }
}