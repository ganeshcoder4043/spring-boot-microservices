package com.microservice.user.service.services;

import com.microservice.user.service.entities.User;

import java.util.List;

public interface UserService {


    // Create user
    User saveUser(User user);

    // Get user by ID
    User getUserById(String userId);

    // Get all users
    List<User> getAllUsers();

    // Update user (pass entire updated user object)
    User updateUser(String userId, User user);

    // Delete user by ID
    String deleteUserById(String userId);


}
