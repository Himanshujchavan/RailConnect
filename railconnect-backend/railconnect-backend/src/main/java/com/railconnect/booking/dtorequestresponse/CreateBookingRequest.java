package com.railconnect.booking.dtorequestresponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateBookingRequest(
        @NotNull Long userId,
        @NotEmpty @Valid List<PassengerRequest> passengers
) {
}