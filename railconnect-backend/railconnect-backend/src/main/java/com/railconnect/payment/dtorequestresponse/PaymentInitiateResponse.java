package com.railconnect.payment.dtorequestresponse;

import com.railconnect.common.enums.PaymentStatus;

public record PaymentInitiateResponse(
        Long paymentId,
        Long bookingId,
        String transactionRef,
        Double amount,
        PaymentStatus status,
        String gatewaySignature
) {
    public PaymentInitiateResponse(Long paymentId, Long bookingId, String transactionRef, Double amount, PaymentStatus status) {
        this(paymentId, bookingId, transactionRef, amount, status, null);
    }
}
