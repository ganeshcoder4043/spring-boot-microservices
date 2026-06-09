package com.microservice.user.service.services.impl;

import com.microservice.user.service.entities.User;
import com.microservice.user.service.exception.ResourceNotFoundException;
import com.microservice.user.service.exception.UserNotFoundException;
import com.microservice.user.service.repositories.UserRepository;
import com.microservice.user.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {

        // Generate Unique Id
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(String userId, User user) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found & Something Went Wrong!!!"));
        existingUser.setName(user.getName());
        existingUser.setLocation(user.getLocation());
        existingUser.setAbout(user.getAbout());
        return userRepository.save(existingUser);

    }

    @Override
    public String deleteUserById(String userId) {
        userRepository.deleteById(userId);
        return "Your User "+userId+" Has Been Deleted";
    }
}
