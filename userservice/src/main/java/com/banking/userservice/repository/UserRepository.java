package com.banking.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.userservice.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);
}
