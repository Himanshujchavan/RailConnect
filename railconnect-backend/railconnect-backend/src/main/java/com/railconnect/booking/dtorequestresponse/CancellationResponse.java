package com.railconnect.booking.dtorequestresponse;

import com.railconnect.common.enums.BookingStatus;

public record CancellationResponse(
        Long bookingId,
        BookingStatus status,
        int seatsFreed,
        String message
) {
}