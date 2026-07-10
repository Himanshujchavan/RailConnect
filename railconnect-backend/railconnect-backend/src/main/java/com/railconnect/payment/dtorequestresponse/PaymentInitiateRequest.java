package com.railconnect.payment.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentInitiateRequest(
        @NotNull Long bookingId,

        // e.g. CARD, UPI, NETBANKING, WALLET
        @NotBlank String paymentMethod
) {
}
