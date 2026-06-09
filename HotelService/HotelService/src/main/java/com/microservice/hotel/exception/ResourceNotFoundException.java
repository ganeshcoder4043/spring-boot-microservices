package com.microservice.hotel.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Constructor with custom message
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor with resource name, field name, field value (for standard formatting)
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}