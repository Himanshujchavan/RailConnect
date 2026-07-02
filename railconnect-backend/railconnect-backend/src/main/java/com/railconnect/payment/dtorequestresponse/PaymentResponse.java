package com.railconnect.payment.dtorequestresponse;

import com.railconnect.common.enums.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Double amount,
        LocalDateTime paidAt,
        PaymentStatus status,
        Long bookingId
) {
}