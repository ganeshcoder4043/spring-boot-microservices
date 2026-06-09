package com.microservice.user.service.exception;

public class BadApiRequestException extends  RuntimeException{

    public BadApiRequestException(){
        super("Bad API request!");
    }

    public BadApiRequestException(String message){
        super(message);
    }
}
