package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends ApplicationException {

    public DuplicateUserException(String value) {
        super(HttpStatus.CONFLICT, "User already exists: " + value);
    }
}