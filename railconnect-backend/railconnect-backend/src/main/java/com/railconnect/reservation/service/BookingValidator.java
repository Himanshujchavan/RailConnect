package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.common.exception.InvalidRequestException;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.entity.Coach;
import com.railconnect.entity.User;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.train.repository.TrainRepository;
import com.railconnect.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Phase 4 — Reservation Engine.
 * <p>
 * All pre-booking validation lives here so {@code BookingServiceImpl} no longer has to
 * inline resource lookups and business rule checks. Each rule is exposed as its own
 * small method so it can be reused or unit tested independently, and {@link #validate}
 * runs the full set for the reservation flow used by {@link BookingOrchestrator}.
 */
@Component
public class BookingValidator {

    private static final int MAX_PASSENGERS_PER_BOOKING = 6;

    private final UserRepository userRepository;
    private final CoachRepository coachRepository;
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;

    public BookingValidator(UserRepository userRepository,
                             CoachRepository coachRepository,
                             TrainRepository trainRepository,
                             ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.coachRepository = coachRepository;
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * Runs every booking-time validation rule and returns the entities that were
     * resolved along the way, so callers don't have to re-fetch them.
     */
    public ValidatedBooking validate(CreateBookingRequest request) {
        User user = validateUser(request.userId());
        Coach coach = validateCoach(request.coachId());
        validateTrain(coach);
        validateSchedule(request.scheduleId());
        validateJourneyDate(request.journeyDate());
        validatePassengerCount(request.passengers().size());
        return new ValidatedBooking(user, coach);
    }

    public User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public Coach validateCoach(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach", "id", coachId));
        if (Boolean.FALSE.equals(coach.getIsActive())) {
            throw new InvalidRequestException("Coach " + coach.getCoachNumber() + " is not active");
        }
        return coach;
    }

    public void validateTrain(Coach coach) {
        trainRepository.findById(coach.getTrain().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Train", "id", coach.getTrain().getId()));
    }

    public void validateSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Schedule", "id", scheduleId);
        }
    }

    public void validateJourneyDate(LocalDate journeyDate) {
        if (journeyDate == null) {
            throw new InvalidRequestException("Journey date is required");
        }
        if (journeyDate.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Journey date cannot be in the past");
        }
    }

    public void validatePassengerCount(int passengerCount) {
        if (passengerCount <= 0) {
            throw new InvalidRequestException("At least one passenger is required for a booking");
        }
        if (passengerCount > MAX_PASSENGERS_PER_BOOKING) {
            throw new InvalidRequestException(
                    "A single booking cannot include more than " + MAX_PASSENGERS_PER_BOOKING + " passengers");
        }
    }

    /**
     * Entities resolved while validating a booking request, handed back so the
     * orchestrator doesn't need a second round trip to the database.
     */
    public record ValidatedBooking(User user, Coach coach) {
    }
}