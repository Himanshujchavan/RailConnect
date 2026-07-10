package com.railconnect.payment.dtorequestresponse;

import java.util.List;

public record PaymentHistoryResponse(
        PaymentResponse payment,
        List<TransactionResponse> transactions,
        List<RefundResponse> refunds
) {
}
