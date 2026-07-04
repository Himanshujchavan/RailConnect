package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.BookingConfirmationResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;

public interface BookingService {
    BookingConfirmationResponse createBooking(CreateBookingRequest request);
}