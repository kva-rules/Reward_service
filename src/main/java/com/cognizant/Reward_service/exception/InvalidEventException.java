package com.cognizant.Reward_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEventException extends RuntimeException {
    
    public InvalidEventException(String message) {
        super(message);
    }
    
    public InvalidEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
