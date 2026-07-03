package com.railconnect.common.exception;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends ApplicationException {

    public BookingNotFoundException(Long bookingId) {
        super(HttpStatus.NOT_FOUND, "Booking not found with id: " + bookingId);
    }
}