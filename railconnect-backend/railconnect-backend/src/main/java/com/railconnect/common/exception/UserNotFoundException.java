package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
    }
}