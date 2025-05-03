package com.banking.userservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.userservice.dto.RegisterRequest;
import com.banking.userservice.dto.UserResponse;
import com.banking.userservice.model.User;
import com.banking.userservice.repository.UserRepository;

import jakarta.validation.Valid;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public UserResponse register(@Valid RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPostalCode(request.getPostalCode());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setSsn(request.getSsn());

        User savedUser = repository.save(user);
        UserResponse userResponse = new UserResponse();

        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setAddress(savedUser.getAddress());
        userResponse.setCity(savedUser.getCity());
        userResponse.setState(savedUser.getState());
        userResponse.setPostalCode(savedUser.getPostalCode());
        userResponse.setDateOfBirth(savedUser.getDateOfBirth());
        userResponse.setSsn(savedUser.getSsn());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());
        return userResponse;
    }

    public UserResponse getUserProfile(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setAddress(user.getAddress());
        userResponse.setCity(user.getCity());
        userResponse.setState(user.getState());
        userResponse.setPostalCode(user.getPostalCode());
        userResponse.setDateOfBirth(user.getDateOfBirth());
        userResponse.setSsn(user.getSsn());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());

        return userResponse;
    }
    
    public List<UserResponse> getAllUsers() {
        List<User> users = repository.findAll();
        return users.stream()
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setEmail(user.getEmail());
                    response.setPassword(user.getPassword());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setAddress(user.getAddress());
                    response.setCity(user.getCity());
                    response.setState(user.getState());
                    response.setPostalCode(user.getPostalCode());
                    response.setDateOfBirth(user.getDateOfBirth());
                    response.setSsn(user.getSsn());
                    response.setCreatedAt(user.getCreatedAt());
                    response.setUpdatedAt(user.getUpdatedAt());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
