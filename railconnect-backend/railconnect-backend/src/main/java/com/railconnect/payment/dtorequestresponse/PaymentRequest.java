package com.railconnect.payment.dtorequestresponse;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull Long bookingId,
        @NotNull Double amount
) {
}