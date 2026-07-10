package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class RefundNotAllowedException extends ApplicationException {

    public RefundNotAllowedException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
