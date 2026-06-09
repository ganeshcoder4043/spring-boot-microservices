package com.microservice.user.service.controller;

import com.microservice.user.service.entities.User;
import com.microservice.user.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<User> saveUser(@RequestBody User user){
        User user1 = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId){
        User userById = userService.getUserById(userId);
        return ResponseEntity.ok(userById);
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> allUsers = userService.getAllUsers();

        if (allUsers.isEmpty()){
            return ResponseEntity.noContent().build(); // No Content
        }

        return ResponseEntity.ok(allUsers);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User user){
        User user1 = userService.updateUser(userId, user);
        return ResponseEntity.ok(user1);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable String userId){
        String deleted = userService.deleteUserById(userId);
        return ResponseEntity.ok(deleted);
    }
}
