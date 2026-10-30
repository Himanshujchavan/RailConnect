package com.railconnect.payment.dtorequestresponse;

public record RefundEstimateResponse(
        Long bookingId,
        Long paymentId,
        Double totalFarePaid,
        Double alreadyRefunded,
        Double cancellationFee,
        Double refundableAmount,
        String policyApplied
) {
}
