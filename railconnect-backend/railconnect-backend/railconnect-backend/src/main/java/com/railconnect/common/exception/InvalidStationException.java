package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidStationException extends ApplicationException {

    public InvalidStationException(String stationCode) {
        super(HttpStatus.BAD_REQUEST, "Invalid station: " + stationCode);
    }
}