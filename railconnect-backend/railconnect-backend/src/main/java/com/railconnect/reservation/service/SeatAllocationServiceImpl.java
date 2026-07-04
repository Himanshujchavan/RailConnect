package com.railconnect.reservation.service;

import com.railconnect.common.util.SeatAllocationUtil;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Seat;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.train.repository.CoachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatAllocationServiceImpl implements SeatAllocationService {

    private final CoachRepository coachRepository;
    private final SeatAllocationRepository seatAllocationRepository;

    public SeatAllocationServiceImpl(
            CoachRepository coachRepository,
            SeatAllocationRepository seatAllocationRepository) {

        this.coachRepository = coachRepository;
        this.seatAllocationRepository = seatAllocationRepository;
    }

    /**
     * Main seat allocation API.
     * Currently delegates to family allocation.
     * Later this will support:
     * - Berth preference
     * - RAC
     * - Waiting List
     */
    @Override
    @Transactional
    public List<SeatAllocation> allocateSeats(
            Long scheduleId,
            Long trainId,
            LocalDate journeyDate,
            String berthPreference,
            int passengerCount) {

        // TODO:
        // Convert berthPreference String -> BerthType
        // Apply preferred berth allocation.

        return allocateFamilySeats(
                scheduleId,
                trainId,
                journeyDate,
                passengerCount
        );
    }

    @Override
    @Transactional
    public List<SeatAllocation> allocateFamilySeats(
            Long scheduleId,
            Long trainId,
            LocalDate journeyDate,
            int partySize) {

        List<Coach> coaches = coachRepository.findByTrainId(trainId);

        List<Seat> allPhysicalSeats = coaches.stream()
                .filter(Coach::getIsActive)
                .flatMap(coach -> coach.getSeats().stream())
                .toList();

        List<SeatAllocation> occupiedSeats =
                seatAllocationRepository.findOccupiedSeats(
                        scheduleId,
                        journeyDate
                );

        Set<Long> bookedSeatIds = occupiedSeats.stream()
                .map(sa -> sa.getSeat().getId())
                .collect(Collectors.toSet());

        List<SeatAllocationUtil.SeatInfo> availableSeats = allPhysicalSeats.stream()
                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
                .map(seat -> new SeatAllocationUtil.SeatInfo(
                        seat.getId(),
                        seat.getCoach().getId(),
                        seat.getCoach().getCoachNumber(),
                        seat.getSeatNumber(),
                        seat.getBerthType()
                ))
                .toList();

        List<SeatAllocationUtil.SeatInfo> selectedSeats =
                SeatAllocationUtil.computeOptimalAllocations(
                        availableSeats,
                        partySize
                );

        Set<Long> selectedSeatIds = selectedSeats.stream()
                .map(SeatAllocationUtil.SeatInfo::getSeatId)
                .collect(Collectors.toSet());

        List<SeatAllocation> allocations = new ArrayList<>();

        for (Seat seat : allPhysicalSeats) {

            if (selectedSeatIds.contains(seat.getId())) {

                SeatAllocation allocation = SeatAllocation.builder()
                        .seat(seat)
                        .journeyDate(journeyDate)
                        .allocatedAt(LocalDateTime.now())
                        .status("BOOKED") // Later replace with enum
                        .build();

                allocations.add(allocation);
            }
        }

        return seatAllocationRepository.saveAll(allocations);
    }
}