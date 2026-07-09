package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.BookingConfirmationResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.entity.Booking;

public interface BookingService {

    BookingConfirmationResponse createBooking(CreateBookingRequest request);

    /**
     * Looks up a booking by its PNR code. Returns null if no booking has that PNR,
     * matching the null-check PNRController performs on the result.
     */
    Booking findByPnrCode(String pnrCode);
}
