package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.CancellationResponse;
import com.railconnect.common.enums.BookingStatus;
import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Payment;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.entity.User;
import com.railconnect.notification.service.NotificationService;
import com.railconnect.payment.dtorequestresponse.RefundEstimateResponse;
import com.railconnect.payment.dtorequestresponse.RefundRequest;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.payment.service.RefundCalculationService;
import com.railconnect.payment.service.RefundService;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.security.SecurityUtil;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.service.WaitingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancellationServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private SeatAllocationRepository seatAllocationRepository;
    @Mock
    private CoachRepository coachRepository;
    @Mock
    private WaitingListService waitingListService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RefundService refundService;
    @Mock
    private RefundCalculationService refundCalculationService;

    @InjectMocks
    private CancellationServiceImpl cancellationService;

    private User testUser;
    private Booking testBooking;
    private Coach testCoach;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.id = 8L;

        testCoach = Coach.builder()
                .id(20L)
                .coachType("3A")
                .coachNumber("B1")
                .seatCount(72)
                .position(1)
                .build();

        testBooking = new Booking();
        testBooking.id = 80L;
        testBooking.user = testUser;
        testBooking.coachId = 20L;
        testBooking.scheduleId = 1L;
        testBooking.journeyDate = LocalDate.now().plusDays(5);
        testBooking.status = BookingStatus.CONFIRMED;

        SeatAllocation allocation = new SeatAllocation();
        allocation.setStatus("CONFIRMED");

        BookingPassenger passenger = new BookingPassenger();
        passenger.setSeatAllocation(allocation);

        List<BookingPassenger> passengers = new ArrayList<>();
        passengers.add(passenger);
        testBooking.bookingPassengers = passengers;

        testPayment = new Payment();
        testPayment.id = 800L;
        testPayment.booking = testBooking;
        testPayment.amount = 900.0;
        testPayment.refundedAmount = 0.0;
        testPayment.status = PaymentStatus.PAID;
    }

    @Test
    @DisplayName("Should cancel booking, free seats, promote queue, and trigger automatic refund for paid bookings")
    void testCancelBookingWithAutomaticRefundSuccess() {
        when(bookingRepository.findById(80L)).thenReturn(Optional.of(testBooking));
        when(coachRepository.findById(20L)).thenReturn(Optional.of(testCoach));
        when(paymentRepository.findByBookingId(80L)).thenReturn(Optional.of(testPayment));

        RefundEstimateResponse estimate = new RefundEstimateResponse(
                80L, 800L, 900.0, 0.0, 90.0, 810.0, "> 48 hours: 10% fee");
        when(refundCalculationService.calculateRefund(eq(testBooking), eq(testPayment), any())).thenReturn(estimate);

        CancellationResponse response = cancellationService.cancelBooking(80L, "Personal reasons");

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED, response.status());
        assertEquals(1, response.seatsFreed());
        assertTrue(response.message().contains("Refund processed: Rs 810.00"));

        verify(securityUtil).verifyUserOwnershipOrAdmin(eq(8L), anyString());
        verify(bookingRepository).save(testBooking);
        verify(waitingListService).processUpgradesOnCancellation(eq(1L), any(), eq("3A"));
        verify(refundService).initiateRefund(any(RefundRequest.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when non-owner/non-admin attempts to cancel booking")
    void testCancelBookingAccessDenied() {
        when(bookingRepository.findById(80L)).thenReturn(Optional.of(testBooking));
        doThrow(new AccessDeniedException("Access denied")).when(securityUtil)
                .verifyUserOwnershipOrAdmin(eq(8L), anyString());

        assertThrows(AccessDeniedException.class, () -> cancellationService.cancelBooking(80L, "Hacking"));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when booking is already cancelled")
    void testCancelAlreadyCancelledBooking() {
        testBooking.status = BookingStatus.CANCELLED;
        when(bookingRepository.findById(80L)).thenReturn(Optional.of(testBooking));

        assertThrows(ResponseStatusException.class, () -> cancellationService.cancelBooking(80L, "Duplicate cancel"));
    }
}
