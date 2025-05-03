package com.bankapp.userservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private LocalDate dateOfBirth;
    private String ssn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
