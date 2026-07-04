package com.railconnect.reservation.service;

import com.railconnect.common.enums.BerthType;
import com.railconnect.common.util.SeatAllocationUtil;
import com.railconnect.entity.Seat;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.entity.Schedule;
import com.railconnect.train.repository.SeatRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeatAllocationServiceImpl implements SeatAllocationService {

    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatAllocationRepository seatAllocationRepository;

    public SeatAllocationServiceImpl(SeatRepository seatRepository,
                                     ScheduleRepository scheduleRepository,
                                     SeatAllocationRepository seatAllocationRepository) {
        this.seatRepository = seatRepository;
        this.scheduleRepository = scheduleRepository;
        this.seatAllocationRepository = seatAllocationRepository;
    }

    @Override
    @Transactional
    public List<Long> allocateSeats(Long scheduleId, Long coachId, LocalDate journeyDate, String preference, int passengerCount) {
        List<Seat> coachSeats = seatRepository.findByCoachId(coachId);
        if (coachSeats.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        Set<Long> occupiedSeatIds = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate).stream()
            .map(allocation -> allocation.getSeat().getId())
                .collect(Collectors.toSet());

        List<SeatAllocationUtil.SeatInfo> availableSeats = coachSeats.stream()
                .filter(seat -> !occupiedSeatIds.contains(seat.getId()))
                .map(SeatSeatInfo::new)
                .toList();

        if (availableSeats.size() < passengerCount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough seats available");
        }

        BerthType berthPreference = parsePreference(preference);
        List<Long> selectedSeatIds = SeatAllocationUtil.filterSeatsByPreference(availableSeats, berthPreference, passengerCount);
        if (selectedSeatIds.size() < passengerCount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unable to allocate requested seats");
        }

        List<Long> allocatedSeatIds = new ArrayList<>();
        for (Long seatId : selectedSeatIds) {
            Seat seat = coachSeats.stream()
                    .filter(candidate -> candidate.getId().equals(seatId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

            SeatAllocation allocation = SeatAllocation.builder()
                    .seat(seat)
                    .schedule(schedule)
                    .journeyDate(journeyDate)
                    .allocatedAt(LocalDateTime.now())
                    .status("BOOKED")
                    .build();
            seatAllocationRepository.save(allocation);
            allocatedSeatIds.add(seatId);
        }

        return allocatedSeatIds;
    }

    @Override
    @Transactional
    public void releaseSeat(Long seatId, Long scheduleId, LocalDate journeyDate) {
        List<SeatAllocation> allocations = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate);
        allocations.stream()
            .filter(allocation -> allocation.getSeat() != null && seatId.equals(allocation.getSeat().getId()))
            .forEach(allocation -> allocation.setStatus("CANCELLED"));
        seatAllocationRepository.saveAll(allocations);
    }

    private BerthType parsePreference(String preference) {
        if (preference == null || preference.isBlank()) {
            return null;
        }
        return BerthType.valueOf(preference.trim().toUpperCase().replace(' ', '_'));
    }

    private static final class SeatSeatInfo implements SeatAllocationUtil.SeatInfo {
        private final Seat seat;

        private SeatSeatInfo(Seat seat) {
            this.seat = seat;
        }

        @Override
        public Long getSeatId() {
            return seat.getId();
        }

        @Override
        public BerthType getBerthType() {
            return BerthType.valueOf(seat.getBerthType().trim().replace(' ', '_'));
        }
    }
}