package com.railconnect.payment.dtorequestresponse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RefundRequest(
        @NotNull Long paymentId,

        // Optional - full refund of the remaining refundable amount if omitted.
        @Positive Double amount,

        String reason
) {
}
