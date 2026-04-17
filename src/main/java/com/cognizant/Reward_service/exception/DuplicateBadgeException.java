package com.cognizant.Reward_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateBadgeException extends RuntimeException {
    
    public DuplicateBadgeException(String message) {
        super(message);
    }
    
    public DuplicateBadgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
