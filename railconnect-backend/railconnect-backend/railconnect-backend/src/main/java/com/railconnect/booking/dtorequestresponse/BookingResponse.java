package com.railconnect.booking.dtorequestresponse;

import com.railconnect.common.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        LocalDateTime createdAt,
        BookingStatus status,
        Long userId,
        List<Long> bookingPassengerIds
) {
}