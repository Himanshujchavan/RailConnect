package com.railconnect.payment.service;

import com.railconnect.common.enums.BookingStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.entity.Schedule;
import com.railconnect.payment.dtorequestresponse.RefundEstimateResponse;
import com.railconnect.train.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefundCalculationServiceImplTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private RefundCalculationServiceImpl calculationService;

    private Booking booking;
    private Payment payment;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.id = 10L;
        booking.scheduleId = 1L;
        booking.status = BookingStatus.CONFIRMED;
        booking.journeyDate = LocalDate.of(2026, 11, 10);

        schedule = new Schedule();
        schedule.setDepartureTime(LocalTime.of(10, 0));

        payment = new Payment();
        payment.id = 100L;
        payment.amount = 1000.0;
        payment.refundedAmount = 0.0;
    }

    @Test
    @DisplayName("Should deduct 10% when cancelled more than 48 hours before departure")
    void testCalculateRefundMoreThan48Hours() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        // Journey is 2026-11-10 10:00, cancel on 2026-11-05 10:00 (> 48h)
        LocalDateTime cancelTime = LocalDateTime.of(2026, 11, 5, 10, 0);

        RefundEstimateResponse estimate = calculationService.calculateRefund(booking, payment, cancelTime);

        assertEquals(1000.0, estimate.totalFarePaid());
        assertEquals(100.0, estimate.cancellationFee());
        assertEquals(900.0, estimate.refundableAmount());
        assertTrue(estimate.policyApplied().contains("> 48 hours"));
    }

    @Test
    @DisplayName("Should deduct 25% when cancelled between 12 and 48 hours before departure")
    void testCalculateRefundBetween12And48Hours() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        // Journey is 2026-11-10 10:00, cancel 24h prior on 2026-11-09 10:00
        LocalDateTime cancelTime = LocalDateTime.of(2026, 11, 9, 10, 0);

        RefundEstimateResponse estimate = calculationService.calculateRefund(booking, payment, cancelTime);

        assertEquals(250.0, estimate.cancellationFee());
        assertEquals(750.0, estimate.refundableAmount());
        assertTrue(estimate.policyApplied().contains("Between 12 and 48 hours"));
    }

    @Test
    @DisplayName("Should deduct 50% when cancelled between 4 and 12 hours before departure")
    void testCalculateRefundBetween4And12Hours() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        // Journey is 2026-11-10 10:00, cancel 6h prior on 2026-11-10 04:00
        LocalDateTime cancelTime = LocalDateTime.of(2026, 11, 10, 4, 0);

        RefundEstimateResponse estimate = calculationService.calculateRefund(booking, payment, cancelTime);

        assertEquals(500.0, estimate.cancellationFee());
        assertEquals(500.0, estimate.refundableAmount());
        assertTrue(estimate.policyApplied().contains("Between 4 and 12 hours"));
    }

    @Test
    @DisplayName("Should be non-refundable when cancelled less than 4 hours before departure")
    void testCalculateRefundLessThan4Hours() {
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        // Journey is 2026-11-10 10:00, cancel 2h prior on 2026-11-10 08:00
        LocalDateTime cancelTime = LocalDateTime.of(2026, 11, 10, 8, 0);

        RefundEstimateResponse estimate = calculationService.calculateRefund(booking, payment, cancelTime);

        assertEquals(1000.0, estimate.cancellationFee());
        assertEquals(0.0, estimate.refundableAmount());
        assertTrue(estimate.policyApplied().contains("Chart Prepared"));
    }

    @Test
    @DisplayName("Should apply flat nominal fee for waiting list cancellation")
    void testCalculateRefundWaitingList() {
        booking.status = BookingStatus.WAITING_LIST;

        RefundEstimateResponse estimate = calculationService.calculateRefund(booking, payment, LocalDateTime.now());

        assertEquals(60.0, estimate.cancellationFee());
        assertEquals(940.0, estimate.refundableAmount());
        assertTrue(estimate.policyApplied().contains("RAC/Waitlist"));
    }
}
