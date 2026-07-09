package com.railconnect.reservation.service;

import com.railconnect.common.enums.BerthType;
import com.railconnect.common.util.SeatAllocationUtil;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Seat;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sole implementation of {@link SeatAllocationService}.
 * <p>
 * Operates on the single coach the passenger chose via {@code CreateBookingRequest.coachId()},
 * matching the "Choose Coach -> Passengers -> Allocate Seats" flow the booking API is built
 * around, rather than scanning every coach on the train.
 */
@Service
public class SeatAllocationServiceImpl implements SeatAllocationService {

    private final CoachRepository coachRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatAllocationRepository seatAllocationRepository;

    public SeatAllocationServiceImpl(CoachRepository coachRepository,
                                     ScheduleRepository scheduleRepository,
                                     SeatAllocationRepository seatAllocationRepository) {
        this.coachRepository = coachRepository;
        this.scheduleRepository = scheduleRepository;
        this.seatAllocationRepository = seatAllocationRepository;
    }

    @Override
    @Transactional
    public List<SeatAllocation> allocateSeats(Long scheduleId, Long coachId, LocalDate journeyDate,
                                              String berthPreference, int passengerCount) {
        Coach coach = resolveCoach(coachId);
        Schedule schedule = resolveSchedule(scheduleId);
        List<SeatAllocationUtil.SeatInfo> availableSeats = getAvailableSeatInfos(coach, scheduleId, journeyDate);

        if (availableSeats.size() < passengerCount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Not enough seats available in coach " + coach.getCoachNumber());
        }

        BerthType preference = parseBerthType(berthPreference);
        List<Long> selectedSeatIds = SeatAllocationUtil.filterSeatsByPreference(availableSeats, preference, passengerCount);

        if (selectedSeatIds.size() < passengerCount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to allocate requested seats");
        }

        return persistAllocations(selectedSeatIds, coach, schedule, journeyDate);
    }

    @Override
    @Transactional
    public List<SeatAllocation> allocateFamilySeats(Long scheduleId, Long coachId, LocalDate journeyDate, int partySize) {
        Coach coach = resolveCoach(coachId);
        Schedule schedule = resolveSchedule(scheduleId);
        List<SeatAllocationUtil.SeatInfo> availableSeats = getAvailableSeatInfos(coach, scheduleId, journeyDate);

        if (availableSeats.size() < partySize) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Not enough seats available in coach " + coach.getCoachNumber() + " for the group");
        }

        List<SeatAllocationUtil.SeatInfo> selectedSeats = SeatAllocationUtil.computeOptimalAllocations(availableSeats, partySize);
        List<Long> selectedSeatIds = selectedSeats.stream()
                .map(SeatAllocationUtil.SeatInfo::getSeatId)
                .toList();

        return persistAllocations(selectedSeatIds, coach, schedule, journeyDate);
    }

    private Coach resolveCoach(Long coachId) {
        return coachRepository.findById(coachId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found"));
    }

    private Schedule resolveSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
    }

    private List<SeatAllocationUtil.SeatInfo> getAvailableSeatInfos(Coach coach, Long scheduleId, LocalDate journeyDate) {
        Set<Long> bookedSeatIds = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate).stream()
                .map(sa -> sa.getSeat().getId())
                .collect(Collectors.toSet());

        return coach.getSeats().stream()
                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
                .map(seat -> new SeatAllocationUtil.SeatInfo(
                        seat.getId(),
                        coach.getId(),
                        coach.getCoachNumber(),
                        seat.getSeatNumber(),
                        seat.getBerthType()))
                .toList();
    }

    /**
     * Builds and persists the SeatAllocation rows for the chosen seats.
     * Sets {@code schedule} explicitly — a prior version of this method omitted it despite
     * the column being NOT NULL, which would have failed at save time.
     */
    private List<SeatAllocation> persistAllocations(List<Long> seatIds, Coach coach, Schedule schedule, LocalDate journeyDate) {
        Set<Long> seatIdSet = new HashSet<>(seatIds);
        List<SeatAllocation> allocations = new ArrayList<>();

        for (Seat seat : coach.getSeats()) {
            if (seatIdSet.contains(seat.getId())) {
                SeatAllocation allocation = SeatAllocation.builder()
                        .seat(seat)
                        .schedule(schedule)
                        .journeyDate(journeyDate)
                        .allocatedAt(LocalDateTime.now())
                        .status("BOOKED")
                        .build();
                allocations.add(allocation);
            }
        }

        return seatAllocationRepository.saveAll(allocations);
    }

    private BerthType parseBerthType(String berthPreference) {
        if (berthPreference == null || berthPreference.isBlank()) {
            return null;
        }
        try {
            return BerthType.valueOf(berthPreference.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
