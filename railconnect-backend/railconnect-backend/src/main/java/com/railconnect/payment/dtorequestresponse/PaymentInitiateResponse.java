package com.railconnect.payment.dtorequestresponse;

import com.railconnect.common.enums.PaymentStatus;

public record PaymentInitiateResponse(
        Long paymentId,
        Long bookingId,
        String transactionRef,
        Double amount,
        PaymentStatus status
) {
}
