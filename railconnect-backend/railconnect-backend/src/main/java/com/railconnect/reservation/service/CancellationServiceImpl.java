package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.CancellationResponse;
import com.railconnect.common.enums.BookingStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.Coach;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationType;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.service.NotificationService;
import com.railconnect.realtime.event.SeatAvailabilityChangedEvent;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.service.WaitingListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Phase 3 — Cancellation.
 * <p>
 * Flow: Cancel Booking -> Free Seats -> Upgrade RAC -> Upgrade WL (handled inside
 * {@link WaitingListService#processUpgradesOnCancellation}, which was already built but had
 * nothing calling it until now).
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

    public CancellationServiceImpl(BookingRepository bookingRepository,
                                    SeatAllocationRepository seatAllocationRepository,
                                    CoachRepository coachRepository,
                                    WaitingListService waitingListService,
                                    NotificationService notificationService,
                                    ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.coachRepository = coachRepository;
        this.waitingListService = waitingListService;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public CancellationResponse cancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

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

        return new CancellationResponse(
                booking.id,
                booking.status,
                seatsFreed,
                "Booking cancelled" + (reason != null && !reason.isBlank() ? " (" + reason + ")" : "")
        );
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