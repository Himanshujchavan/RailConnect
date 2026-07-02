package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class SeatUnavailableException extends ApplicationException {

    public SeatUnavailableException(String seatNumber) {
        super(HttpStatus.CONFLICT, "Seat is unavailable: " + seatNumber);
    }
}