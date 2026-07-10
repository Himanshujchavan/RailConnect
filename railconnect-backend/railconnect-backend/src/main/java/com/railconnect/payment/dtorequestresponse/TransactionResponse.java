package com.railconnect.payment.dtorequestresponse;

import com.railconnect.common.enums.TransactionStatus;
import com.railconnect.common.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        String transactionRef,
        TransactionType type,
        TransactionStatus status,
        Double amount,
        String gatewayResponse,
        LocalDateTime createdAt
) {
}
