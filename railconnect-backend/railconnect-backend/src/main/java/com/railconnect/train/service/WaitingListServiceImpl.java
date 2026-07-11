package com.railconnect.train.service;

import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.WaitingListAssignment;
import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationType;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.service.NotificationService;
import com.railconnect.reservation.repository.BookingPassengerRepository;
import com.railconnect.train.repository.WaitingListAssignmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WaitingListServiceImpl implements WaitingListService {

    private static final Logger logger = LoggerFactory.getLogger(WaitingListServiceImpl.class);

    private final WaitingListAssignmentRepository wlRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final NotificationService notificationService;

    // Fixed capacity configuration thresholds for the demo rules
    private static final int MAX_RAC_LIMIT = 5;
    private static final int MAX_WL_LIMIT = 10;

    public WaitingListServiceImpl(WaitingListAssignmentRepository wlRepository,
                                   BookingPassengerRepository bookingPassengerRepository,
                                   NotificationService notificationService) {
        this.wlRepository = wlRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public WaitingListAssignment addToQueue(Long scheduleId, LocalDate journeyDate, Long passengerId, int remainingAvailableSeats) {
        // If remaining physical seats are <= 0, determine RAC or WL status placement
        String queueType;
        int currentQueueSize;

        // Fetch current structural counts to calculate queue numbers
        int currentRacCount = wlRepository.findMaxPriorityNumber(scheduleId, journeyDate, "RAC");
        
        if (currentRacCount < MAX_RAC_LIMIT) {
            queueType = "RAC";
            currentQueueSize = currentRacCount;
        } else {
            queueType = "WL";
            currentQueueSize = wlRepository.findMaxPriorityNumber(scheduleId, journeyDate, "WL");
            if (currentQueueSize >= MAX_WL_LIMIT) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TRAIN REGRET: Waiting List allocation limits reached.");
            }
        }

        WaitingListAssignment assignment = WaitingListAssignment.builder()
                .schedule(null) // Associated implicitly or load entity via repository reference
                .journeyDate(journeyDate)
                .bookingPassengerId(passengerId)
                .queueType(queueType)
                .priorityNumber(currentQueueSize + 1)
                .createdAt(LocalDateTime.now())
                .currentStatus("ACTIVE")
                .build();

        return wlRepository.save(assignment);
    }

    @Override
    @Transactional
    public void processUpgradesOnCancellation(Long scheduleId, LocalDate journeyDate, String vacatedCoachType) {
        // Step 1: Look for an RAC passenger to upgrade to CONFIRMED
        Optional<WaitingListAssignment> nextRac = wlRepository
                .findFirstByScheduleIdAndJourneyDateAndQueueTypeAndCurrentStatusOrderByPriorityNumberAsc(
                        scheduleId, journeyDate, "RAC", "ACTIVE");

        if (nextRac.isPresent()) {
            WaitingListAssignment racPassenger = nextRac.get();
            racPassenger.setCurrentStatus("UPGRADED");
            wlRepository.save(racPassenger);
            notify(racPassenger.getBookingPassengerId(), NotificationType.RAC_UPGRADED,
                    "Your RAC ticket has been upgraded to a confirmed berth.");

            // Step 2: Cascade move the top WL passenger into the now open RAC slot
            Optional<WaitingListAssignment> nextWl = wlRepository
                    .findFirstByScheduleIdAndJourneyDateAndQueueTypeAndCurrentStatusOrderByPriorityNumberAsc(
                            scheduleId, journeyDate, "WL", "ACTIVE");

            if (nextWl.isPresent()) {
                WaitingListAssignment wlPassenger = nextWl.get();
                wlPassenger.setCurrentStatus("UPGRADED");
                wlRepository.save(wlPassenger);

                // Re-add them into the RAC queue structure cleanly
                int currentRacMax = wlRepository.findMaxPriorityNumber(scheduleId, journeyDate, "RAC");
                WaitingListAssignment movedToRac = WaitingListAssignment.builder()
                        .journeyDate(journeyDate)
                        .bookingPassengerId(wlPassenger.getBookingPassengerId())
                        .queueType("RAC")
                        .priorityNumber(currentRacMax + 1)
                        .createdAt(LocalDateTime.now())
                        .currentStatus("ACTIVE")
                        .build();
                wlRepository.save(movedToRac);
                notify(wlPassenger.getBookingPassengerId(), NotificationType.WL_PROMOTED,
                        "Your waiting list ticket has moved up to RAC.");
            }
        }
    }

    /**
     * Resolves the passenger's account from their {@link BookingPassenger} row and fires the
     * notification. Never lets a notification failure interrupt the upgrade cascade itself.
     */
    private void notify(Long bookingPassengerId, NotificationType type, String details) {
        try {
            BookingPassenger bookingPassenger = bookingPassengerRepository.findById(bookingPassengerId)
                    .orElse(null);
            if (bookingPassenger == null || bookingPassenger.booking == null || bookingPassenger.booking.user == null) {
                return;
            }

            notificationService.send(new NotificationSendRequest(
                    bookingPassenger.booking.user.id,
                    type,
                    List.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP),
                    details,
                    null,
                    null));
        } catch (Exception ex) {
            logger.warn("Failed to send {} notification for bookingPassengerId {}: {}",
                    type, bookingPassengerId, ex.getMessage());
        }
    }
}