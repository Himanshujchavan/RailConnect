package com.railconnect.payment.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;

/**
 * Body posted back by the payment gateway to /payment/success and /payment/failure.
 */
public record PaymentCallbackRequest(
        @NotBlank String transactionRef,

        // Gateway reference / auth code on success, failure reason on failure. Optional either way.
        String gatewayReference,

        // Cryptographic HMAC-SHA256 signature generated during payment initiation
        String signature
) {
    public PaymentCallbackRequest(String transactionRef, String gatewayReference) {
        this(transactionRef, gatewayReference, null);
    }
}
