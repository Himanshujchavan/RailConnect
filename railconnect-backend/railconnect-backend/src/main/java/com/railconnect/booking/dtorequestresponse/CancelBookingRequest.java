package com.railconnect.booking.dtorequestresponse;

import jakarta.validation.constraints.NotNull;

public record CancelBookingRequest(
        @NotNull Long bookingId,
        String reason
) {
}