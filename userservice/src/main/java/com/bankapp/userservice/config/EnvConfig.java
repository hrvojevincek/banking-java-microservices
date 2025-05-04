package com.bankapp.userservice.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Configuration class to load environment variables from .env file
 * This is a more flexible alternative to Spring's built-in property sources
 */
@Configuration
public class EnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);
    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Bean
    public Dotenv loadDotenvIfExists(ConfigurableEnvironment environment) {
        // Check if .env file exists
        File dotenvFile = new File(".env");
        if (!dotenvFile.exists()) {
            logger.info(".env file not found. Using environment variables or application properties.");
            return null;
        }

        try {
            logger.info("Loading environment variables from .env file");

            // Load .env file
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .systemProperties()
                    .load();

            // Add .env variables to Spring Environment
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> envMap.put(entry.getKey(), entry.getValue()));

            // Add as a property source with lowest precedence (can be overridden by system
            // properties)
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, envMap));

            logger.info("Successfully loaded environment variables from .env file");
            return dotenv;
        } catch (Exception e) {
            logger.error("Error loading .env file: {}", e.getMessage(), e);
            return null;
        }
    }
}