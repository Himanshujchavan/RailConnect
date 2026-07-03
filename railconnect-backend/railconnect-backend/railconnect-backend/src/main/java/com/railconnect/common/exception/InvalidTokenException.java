package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

/** Covers malformed/unknown/revoked refresh tokens and password-reset tokens. */
public class InvalidTokenException extends ApplicationException {

    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
