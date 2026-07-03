package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidPNRException extends ApplicationException {

    public InvalidPNRException(String code) {
        super(HttpStatus.BAD_REQUEST, "Invalid PNR: " + code);
    }
}