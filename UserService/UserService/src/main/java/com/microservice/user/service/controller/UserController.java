package com.microservice.user.service.controller;

import com.microservice.user.service.entities.User;
import com.microservice.user.service.services.UserService;
import com.microservice.user.service.services.impl.UserServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/create-user")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User user1 = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

   /* @GetMapping("/{userId}")
    @Retry(name = "ratingHotelRetry", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "ratingHotelCircuitBreaker", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        logger.info("🔄 Attempting to fetch user: {}", userId);
        User userById = userService.getUserById(userId);
        return ResponseEntity.ok(userById);
    }

    public ResponseEntity<User> retryFallback(String userId, Exception ex) {
        logger.warn("All retries failed for user: {}, Error: {}", userId, ex.getMessage());
        // Retry fail hone par Circuit Breaker check karega
        throw new RuntimeException(ex); // Circuit Breaker fallback trigger karega
    }

    public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex) {
        logger.info("Circuit Breaker OPEN! Returning dummy user :{} ", ex.getMessage());
        User dummy = User.builder()
                .name("Dummy")
                .email("dummy@gmail.com")
                .location("Dummy-NCR")
                .about("SERVICE IS DOWN SO YOU CANT ACCESS DATA FROM RATING SERVICE AND HOTEL SERVICE")
                .build();
        return new ResponseEntity<>(dummy, HttpStatus.OK);
    }*/


    //  Retry + Circuit Breaker
    @GetMapping("/{userId}")
//    @Retry(name = "ratingHotelRetry", fallbackMethod = "retryFallback")
//    @CircuitBreaker(name = "ratingHotelCircuitBreaker", fallbackMethod = "ratingHotelFallback")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        logger.info("Attempting to fetch user: {}", userId);
        User userById = userService.getUserById(userId);
        return ResponseEntity.ok(userById);
    }

    //  Retry Fallback
    public ResponseEntity<User> retryFallback(String userId, Exception ex) {
        logger.warn("All retries failed for user: {}, Error: {}", userId, ex.getMessage());
        // Retry fail hone par Circuit Breaker check karega
        throw new RuntimeException(ex); // Circuit Breaker fallback trigger karega
    }

    //  Circuit Breaker Fallback (when circuit is OPEN)
    public ResponseEntity<User> ratingHotelFallback(String userId, Exception ex) {
        logger.info("Circuit Breaker OPEN! Returning dummy user");
        User dummy = User.builder()
                .name("Dummy")
                .email("dummy@gmail.com")
                .location("Dummy-NCR")
                .about("SERVICE IS DOWN SO YOU CAN'T ACCESS DATA")
                .build();
        return new ResponseEntity<>(dummy, HttpStatus.OK);
    }

    // ✅ Rate Limiter Fallback (When limit exceeded)
    public ResponseEntity<User> rateLimiterFallback(String userId, Exception ex) {
        logger.warn("🚫 Rate limit exceeded for user: {}", userId);
        User dummy = User.builder()
                .userId("RATE_LIMITED")
                .name("Too Many Requests")
                .email("rate-limited@example.com")
                .location("N/A")
                .about("Rate limit exceeded. Please try after 10 seconds")
                .build();
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(dummy);
    }



    @GetMapping("/all-user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();

        if (allUsers.isEmpty()) {
            return ResponseEntity.noContent().build(); // No Content
        }

        return ResponseEntity.ok(allUsers);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User user) {
        User user1 = userService.updateUser(userId, user);
        return ResponseEntity.ok(user1);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable String userId) {
        String deleted = userService.deleteUserById(userId);
        return ResponseEntity.ok(deleted);
    }
}
