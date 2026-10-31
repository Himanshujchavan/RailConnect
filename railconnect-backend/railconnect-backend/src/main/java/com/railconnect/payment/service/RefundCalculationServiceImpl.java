package com.railconnect.payment.service;

import com.railconnect.common.enums.BookingStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.entity.Schedule;
import com.railconnect.payment.dtorequestresponse.RefundEstimateResponse;
import com.railconnect.train.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class RefundCalculationServiceImpl implements RefundCalculationService {

    private final ScheduleRepository scheduleRepository;

    public RefundCalculationServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public RefundEstimateResponse calculateRefund(Booking booking, Payment payment, LocalDateTime cancellationTime) {
        if (payment == null || payment.amount == null) {
            return new RefundEstimateResponse(
                    booking.getId(), null, 0.0, 0.0, 0.0, 0.0, "No payment found"
            );
        }

        double alreadyRefunded = payment.refundedAmount != null ? payment.refundedAmount : 0.0;
        double remainingBalance = Math.max(0.0, payment.amount - alreadyRefunded);

        if (remainingBalance <= 0) {
            return new RefundEstimateResponse(
                    booking.getId(), payment.id, payment.amount, alreadyRefunded, 0.0, 0.0,
                    "Payment has already been fully refunded"
            );
        }

        LocalDateTime departureDateTime = getDepartureDateTime(booking);
        LocalDateTime cancelTime = cancellationTime != null ? cancellationTime : LocalDateTime.now();

        // If ticket is in WAITING_LIST or RAC status, apply flat clerkage charge
        if (booking.getStatus() == BookingStatus.WAITING_LIST || booking.getStatus() == BookingStatus.RAC) {
            double fee = Math.min(60.0, remainingBalance);
            double refund = Math.max(0.0, remainingBalance - fee);
            return new RefundEstimateResponse(
                    booking.getId(), payment.id, payment.amount, alreadyRefunded, fee, refund,
                    "RAC/Waitlist cancellation policy: flat nominal charge applied"
            );
        }

        Duration timeUntilDeparture = Duration.between(cancelTime, departureDateTime);
        long hours = timeUntilDeparture.toHours();

        double cancellationFee;
        String policy;

        if (hours >= 48) {
            // More than 48 hours: 10% cancellation fee
            cancellationFee = Math.round((payment.amount * 0.10) * 100.0) / 100.0;
            policy = "> 48 hours before departure: 10% cancellation charge applied";
        } else if (hours >= 12) {
            // Between 12 and 48 hours: 25% cancellation fee
            cancellationFee = Math.round((payment.amount * 0.25) * 100.0) / 100.0;
            policy = "Between 12 and 48 hours before departure: 25% cancellation charge applied";
        } else if (hours >= 4) {
            // Between 4 and 12 hours: 50% cancellation fee
            cancellationFee = Math.round((payment.amount * 0.50) * 100.0) / 100.0;
            policy = "Between 4 and 12 hours before departure: 50% cancellation charge applied";
        } else {
            // Less than 4 hours: chart prepared, non-refundable
            cancellationFee = remainingBalance;
            policy = "< 4 hours before departure (Chart Prepared): Non-refundable";
        }

        double eligibleRefund = Math.max(0.0, remainingBalance - cancellationFee);

        return new RefundEstimateResponse(
                booking.getId(),
                payment.id,
                payment.amount,
                alreadyRefunded,
                cancellationFee,
                eligibleRefund,
                policy
        );
    }

    private LocalDateTime getDepartureDateTime(Booking booking) {
        if (booking.getJourneyDate() == null) {
            return LocalDateTime.now().plusDays(3);
        }
        LocalTime departureTime = LocalTime.of(8, 0); // default morning departure
        // Use the schedule relationship if available
        if (booking.getSchedule() != null) {
            LocalTime schedTime = booking.getSchedule().getDepartureTime();
            if (schedTime != null) {
                departureTime = schedTime;
            }
        }
        return booking.getJourneyDate().atTime(departureTime);
    }
}
