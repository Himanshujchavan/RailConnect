package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class TrainNotFoundException extends ApplicationException {

    public TrainNotFoundException(Long trainId) {
        super(HttpStatus.NOT_FOUND, "Train not found with id: " + trainId);
    }
}