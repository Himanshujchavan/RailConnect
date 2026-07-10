package com.railconnect.payment.dtorequestresponse;

import com.railconnect.common.enums.PaymentStatus;

import java.time.LocalDateTime;

public record InvoiceResponse(
        Long paymentId,
        Long bookingId,
        String pnrCode,
        String transactionRef,
        Double amount,
        Double refundedAmount,
        PaymentStatus status,
        LocalDateTime paidAt,
        LocalDateTime invoiceGeneratedAt
) {
}
