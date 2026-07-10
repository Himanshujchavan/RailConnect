package com.railconnect.payment.dtorequestresponse;

import com.railconnect.common.enums.RefundStatus;

import java.time.LocalDateTime;

public record RefundResponse(
        Long refundId,
        Long paymentId,
        Double amount,
        RefundStatus status,
        String reason,
        LocalDateTime processedAt
) {
}
