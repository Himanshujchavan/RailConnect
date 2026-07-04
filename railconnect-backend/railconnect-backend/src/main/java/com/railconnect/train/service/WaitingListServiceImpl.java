package com.railconnect.train.service;

import com.railconnect.entity.WaitingListAssignment;
import com.railconnect.train.repository.WaitingListAssignmentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WaitingListServiceImpl implements WaitingListService {

    private final WaitingListAssignmentRepository wlRepository;

    // Fixed capacity configuration thresholds for the demo rules
    private static final int MAX_RAC_LIMIT = 5;
    private static final int MAX_WL_LIMIT = 10;

    public WaitingListServiceImpl(WaitingListAssignmentRepository wlRepository) {
        this.wlRepository = wlRepository;
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
            }
        }
    }
}