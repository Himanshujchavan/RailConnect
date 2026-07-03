package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends ApplicationException {

    public EmailAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, "An account with this email already exists: " + email);
    }
}
