package com.railconnect.payment.service;

import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.payment.dtorequestresponse.RefundEstimateResponse;

import java.time.LocalDateTime;

public interface RefundCalculationService {

    /**
     * Calculates the cancellation penalty and eligible refund amount for a booking/payment
     * based on time remaining until departure.
     */
    RefundEstimateResponse calculateRefund(Booking booking, Payment payment, LocalDateTime cancellationTime);
}
