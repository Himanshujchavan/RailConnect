package com.railconnect.booking.dtorequestresponse;

import com.railconnect.common.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingHistoryResponse(
        Long bookingId,
        LocalDateTime createdAt,
        BookingStatus status,
        Long pnrId,
        Long paymentId,
        Integer passengerCount
) {
}