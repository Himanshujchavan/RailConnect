package com.railconnect.pnr.dtorequestresponse;

import com.railconnect.common.enums.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PnrStatusResponse(
        String pnrCode,
        Long bookingId,
        BookingStatus status,
        LocalDate journeyDate,
        Double totalFare,
        String trainNumber,
        String trainName,
        String coachNumber,
        String coachType,
        LocalDateTime bookedAt,
        List<PnrPassengerDto> passengers
) {
    public record PnrPassengerDto(
            Long id,
            String name,
            Integer age,
            String gender,
            String berthPreference,
            String seatNumber,
            String status
    ) {}
}
