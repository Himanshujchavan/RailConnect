package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.CancellationResponse;
import com.railconnect.common.enums.BookingStatus;
import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Payment;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationType;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.service.NotificationService;
import com.railconnect.payment.dtorequestresponse.RefundEstimateResponse;
import com.railconnect.payment.dtorequestresponse.RefundRequest;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.payment.service.RefundCalculationService;
import com.railconnect.payment.service.RefundService;
import com.railconnect.realtime.event.SeatAvailabilityChangedEvent;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.security.SecurityUtil;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.service.WaitingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 3 & 4 — Cancellation and Refund.
 * <p>
 * Flow: Verify Ownership -> Cancel Booking -> Free Seats -> Upgrade RAC -> Upgrade WL
 * -> Process Eligible Refund (Phase 4) -> Send Notification.
 */
@Service
public class CancellationServiceImpl implements CancellationService {

    private static final Logger logger = LoggerFactory.getLogger(CancellationServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final CoachRepository coachRepository;
    private final WaitingListService waitingListService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final SecurityUtil securityUtil;
    private final PaymentRepository paymentRepository;
    private final RefundService refundService;
    private final RefundCalculationService refundCalculationService;

    public CancellationServiceImpl(BookingRepository bookingRepository,
                                    SeatAllocationRepository seatAllocationRepository,
                                    CoachRepository coachRepository,
                                    WaitingListService waitingListService,
                                    NotificationService notificationService,
                                    ApplicationEventPublisher eventPublisher,
                                    SecurityUtil securityUtil,
                                    PaymentRepository paymentRepository,
                                    RefundService refundService,
                                    RefundCalculationService refundCalculationService) {
        this.bookingRepository = bookingRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.coachRepository = coachRepository;
        this.waitingListService = waitingListService;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
        this.securityUtil = securityUtil;
        this.paymentRepository = paymentRepository;
        this.refundService = refundService;
        this.refundCalculationService = refundCalculationService;
    }

    @Override
    @Transactional
    public CancellationResponse cancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        // Ownership Verification: Ensure requester owns the booking or is an admin
        Long ownerId = booking.user != null ? booking.user.id : null;
        securityUtil.verifyUserOwnershipOrAdmin(ownerId, "cancel this booking");

        if (booking.status == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already cancelled");
        }

        int seatsFreed = freeSeats(booking);

        booking.status = BookingStatus.CANCELLED;
        bookingRepository.save(booking);

        if (seatsFreed > 0) {
            eventPublisher.publishEvent(new SeatAvailabilityChangedEvent(booking.scheduleId, booking.journeyDate));
        }

        notifyCancellation(booking, reason);
        promoteQueue(booking, seatsFreed);

        // Phase 4: Automatic refund calculation and processing if payment was completed
        String refundSummary = processAutomaticRefund(booking, reason);

        String message = "Booking cancelled"
                + (reason != null && !reason.isBlank() ? " (" + reason + ")" : "")
                + (refundSummary != null ? refundSummary : "");

        return new CancellationResponse(
                booking.id,
                booking.status,
                seatsFreed,
                message
        );
    }

    /**
     * If a payment exists and is in PAID or PARTIALLY_REFUNDED status, calculate the eligible
     * refund based on departure timing rules and execute the refund.
     */
    private String processAutomaticRefund(Booking booking, String reason) {
        try {
            Payment payment = paymentRepository.findByBookingId(booking.id).orElse(null);
            if (payment == null || (payment.status != PaymentStatus.PAID && payment.status != PaymentStatus.PARTIALLY_REFUNDED)) {
                return null;
            }

            RefundEstimateResponse estimate = refundCalculationService.calculateRefund(booking, payment, LocalDateTime.now());
            if (estimate.refundableAmount() > 0) {
                refundService.initiateRefund(new RefundRequest(
                        payment.id,
                        estimate.refundableAmount(),
                        "Booking cancellation: " + (reason != null ? reason : "User requested")
                ));
                return String.format(" | Refund processed: Rs %.2f (Fee: Rs %.2f, %s)",
                        estimate.refundableAmount(), estimate.cancellationFee(), estimate.policyApplied());
            } else {
                return " | Non-refundable (" + estimate.policyApplied() + ")";
            }
        } catch (Exception ex) {
            logger.warn("Automatic refund processing failed for booking {}: {}", booking.id, ex.getMessage());
            return null;
        }
    }

    /**
     * Marks every seat allocation tied to this booking's passengers as CANCELLED, freeing
     * those seats up for the waiting list to claim.
     */
    private int freeSeats(Booking booking) {
        int freed = 0;
        if (booking.bookingPassengers == null) {
            return freed;
        }

        for (BookingPassenger bookingPassenger : booking.bookingPassengers) {
            SeatAllocation allocation = bookingPassenger.getSeatAllocation();
            if (allocation != null && !"CANCELLED".equals(allocation.getStatus())) {
                allocation.setStatus("CANCELLED");
                seatAllocationRepository.save(allocation);
                freed++;
            }
        }
        return freed;
    }

    /**
     * Fires a BOOKING_CANCELLED notification to the booking's owner. Never lets a delivery
     * failure interrupt the cancellation itself.
     */
    private void notifyCancellation(Booking booking, String reason) {
        if (booking.user == null) {
            return;
        }
        String details = (booking.pnrCode != null ? "PNR " + booking.pnrCode : "")
                + (reason != null && !reason.isBlank() ? " (" + reason + ")" : "");
        try {
            notificationService.send(new NotificationSendRequest(
                    booking.user.id,
                    NotificationType.BOOKING_CANCELLED,
                    List.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP),
                    details.isBlank() ? null : details.trim(),
                    null,
                    null));
        } catch (Exception ex) {
            logger.warn("Failed to send cancellation notification for booking {}: {}", booking.id, ex.getMessage());
        }
    }

    /**
     * Runs the RAC->CONFIRMED / WL->RAC promotion cascade once per freed seat, so a
     * multi-passenger cancellation promotes multiple people rather than just one.
     */
    private void promoteQueue(Booking booking, int seatsFreed) {
        if (seatsFreed <= 0) {
            return;
        }

        String vacatedCoachType = coachRepository.findById(booking.coachId)
                .map(Coach::getCoachType)
                .orElse(null);

        for (int i = 0; i < seatsFreed; i++) {
            waitingListService.processUpgradesOnCancellation(booking.scheduleId, booking.journeyDate, vacatedCoachType);
        }
    }
}
