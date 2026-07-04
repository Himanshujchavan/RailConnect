package com.railconnect.booking.dtorequestresponse;

import com.railconnect.common.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingConfirmationResponse(
        Long bookingId,
        String pnrCode,
        LocalDateTime createdAt,
        BookingStatus status,
        Long userId,
        Long scheduleId,
        Long coachId,
        LocalDate journeyDate,
        Double totalFare,
        List<Long> seatIds,
        List<Long> bookingPassengerIds
) {
}