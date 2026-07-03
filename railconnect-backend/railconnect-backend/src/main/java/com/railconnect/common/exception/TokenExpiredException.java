package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

/** Covers expired refresh tokens and expired password-reset tokens. */
public class TokenExpiredException extends ApplicationException {

    public TokenExpiredException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
