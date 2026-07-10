package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.CancellationResponse;

public interface CancellationService {

    /**
     * Cancels a booking: frees its seats, marks the booking CANCELLED, and triggers
     * RAC/Waiting-List promotion for every seat that was just freed.
     */
    CancellationResponse cancelBooking(Long bookingId, String reason);
}