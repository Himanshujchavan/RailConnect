package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends ApplicationException {

    public PaymentFailedException(String message) {
        super(HttpStatus.PAYMENT_REQUIRED, message);
    }
}