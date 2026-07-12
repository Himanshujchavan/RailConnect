package com.railconnect.dashboard.dtorequestresponse;

import com.railconnect.common.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DashboardBookingResponse(
        Long bookingId,
        String pnrCode,
        BookingStatus status,
        Long scheduleId,
        Long coachId,
        LocalDate journeyDate,
        Double totalFare,
        Integer passengerCount,
        LocalDateTime createdAt
) {
}
