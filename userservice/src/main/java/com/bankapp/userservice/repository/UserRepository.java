package com.bankapp.userservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankapp.userservice.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(
            @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

    Optional<User> findByEmail(String email);

    /**
     * Find user by Cognito username
     */
    Optional<User> findByCognitoUsername(String cognitoUsername);

    /**
     * Find user by Cognito user ID
     */
    Optional<User> findByCognitoUserId(String cognitoUserId);

    /**
     * Find all unverified users created before a specified date
     * 
     * @param date The cutoff date
     * @return List of unverified users
     */
    List<User> findByEmailVerifiedFalseAndCreatedAtBefore(LocalDateTime date);
}
