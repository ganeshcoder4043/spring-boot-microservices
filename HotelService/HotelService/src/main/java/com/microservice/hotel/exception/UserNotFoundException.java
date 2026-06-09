package com.microservice.hotel.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }

}
