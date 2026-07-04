package com.railconnect.train.service;

import com.railconnect.common.enums.BerthType;
import com.railconnect.entity.Seat;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.train.dtorequestresponse.SeatAvailabilityResponse;
import com.railconnect.train.repository.SeatRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeatAvailabilityServiceImpl implements SeatAvailabilityService {

    private final SeatRepository seatRepository;
    private final SeatAllocationRepository seatAllocationRepository;

    public SeatAvailabilityServiceImpl(SeatRepository seatRepository,
                                       SeatAllocationRepository seatAllocationRepository) {
        this.seatRepository = seatRepository;
        this.seatAllocationRepository = seatAllocationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatAvailabilityResponse> getAvailableSeats(Long scheduleId, Long coachId, LocalDate journeyDate) {
        List<Seat> coachSeats = seatRepository.findByCoachId(coachId);
        if (coachSeats.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach seats not found");
        }

        Map<Long, Seat> seatsById = coachSeats.stream()
                .collect(Collectors.toMap(Seat::getId, Function.identity()));

        Set<Long> occupiedSeatIds = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate).stream()
                .map(SeatAllocation::getSeat)
                .map(Seat::getId)
                .collect(Collectors.toSet());

        return coachSeats.stream()
                .filter(seat -> !occupiedSeatIds.contains(seat.getId()))
              .map(seat -> new SeatAvailabilityResponse(
        seat.getId(),
        String.valueOf(seat.getSeatNumber()),
        seat.getBerthType(),
        seat.getCoach().getCoachNumber(),
        true
))
                .toList();
    }
}