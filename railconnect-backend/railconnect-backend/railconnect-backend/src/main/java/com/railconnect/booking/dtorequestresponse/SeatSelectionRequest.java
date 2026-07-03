package com.railconnect.booking.dtorequestresponse;

import jakarta.validation.constraints.NotNull;

public record SeatSelectionRequest(
        @NotNull Long bookingPassengerId,
        @NotNull Long seatId
) {
}