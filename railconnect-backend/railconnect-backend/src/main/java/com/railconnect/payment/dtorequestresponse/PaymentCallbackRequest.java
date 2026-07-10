package com.railconnect.payment.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;

/**
 * Body posted back by the (simulated) payment gateway to /payment/success and /payment/failure.
 */
public record PaymentCallbackRequest(
        @NotBlank String transactionRef,

        // Gateway reference / auth code on success, failure reason on failure. Optional either way.
        String gatewayReference
) {
}
