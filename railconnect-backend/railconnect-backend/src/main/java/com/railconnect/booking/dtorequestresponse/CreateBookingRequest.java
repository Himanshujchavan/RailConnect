package com.railconnect.booking.dtorequestresponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingRequest(
        @NotNull Long userId,
        @NotNull Long scheduleId,
        @NotNull Long coachId,
        @NotNull LocalDate journeyDate,
        @NotEmpty @Valid List<PassengerRequest> passengers
,
        String seatPreference
) {
}