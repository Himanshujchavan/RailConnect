package com.railconnect.reservation.service;

import com.railconnect.common.enums.BerthType;
import com.railconnect.common.util.SeatAllocationUtil;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Seat;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.repository.SeatRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.booking.dtorequestresponse.BookingReviewRequest.PassengerRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeightedSeatAllocationService implements SeatAllocationService {

    private final SeatRepository seatRepository;
    private final SeatAllocationRepository seatAllocationRepository;

    public WeightedSeatAllocationService(SeatRepository seatRepository,
                                        SeatAllocationRepository seatAllocationRepository) {
        this.seatRepository = seatRepository;
        this.seatAllocationRepository = seatAllocationRepository;
    }

    /**
     * Weighted allocation based on passenger preferences.
     * Scoring rules (simplified):
     *   +100 for a block of consecutive seats in the same coach
     *   +20  for seats in the same coach (family grouping)
     *   +50  for each passenger whose berth preference matches the seat's berth type
     *   +20  for each passenger whose quota type matches the seat's quota flag
     */
    @Override
    @Transactional
    public List<SeatAllocation> allocateSeats(Long scheduleId, Long trainId,
                                             LocalDate journeyDate, String berthPreference,
                                             int passengerCount) {
        // Compatibility delegate – creates dummy PassengerRequest objects with the given berth preference.
        List<PassengerRequest> dummyPassengers = Collections.nCopies(passengerCount, new PassengerRequest());
        dummyPassengers.forEach(p -> p.setPreferredBerth(berthPreference));
        return allocateWeightedSeats(scheduleId, trainId, journeyDate, dummyPassengers);
    }

    /**
     * Core weighted allocation implementation.
     */
    @Transactional
    public List<SeatAllocation> allocateWeightedSeats(Long scheduleId, Long trainId,
                                                      LocalDate journeyDate,
                                                      List<PassengerRequest> passengers) {
        int partySize = passengers.size();
        // 1. Gather active coaches for the train
        List<Coach> coaches = seatRepository.findByTrainId(trainId)
                .stream()
                .filter(Coach::getIsActive)
                .collect(Collectors.toList());

        // 2. Flatten all seats, filter out already allocated ones
        List<Seat> allPhysicalSeats = coaches.stream()
                .flatMap(coach -> coach.getSeats().stream())
                .collect(Collectors.toList());

        List<SeatAllocation> occupied = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate);
        var bookedSeatIds = occupied.stream()
                .map(sa -> sa.getSeat().getId())
                .collect(Collectors.toSet());

        List<SeatAllocationUtil.SeatInfo> availableSeats = allPhysicalSeats.stream()
                .filter(seat -> !bookedSeatIds.contains(seat.getId()))
                .map(seat -> new SeatAllocationUtil.SeatInfo(
                        seat.getId(),
                        seat.getCoach().getId(),
                        seat.getCoach().getCoachNumber(),
                        seat.getSeatNumber(),
                        seat.getBerthType()))
                .toList();

        // 3. Build candidate windows of size partySize
        List<List<SeatAllocationUtil.SeatInfo>> windows = buildWindows(availableSeats, partySize);

        // 4. Score each window and pick the best
        List<List<SeatAllocationUtil.SeatInfo>> sortedWindows = windows.stream()
                .sorted(Comparator.comparingInt(w -> -scoreWindow(w, passengers)))
                .collect(Collectors.toList());

        List<SeatAllocationUtil.SeatInfo> bestWindow = sortedWindows.isEmpty() ? List.of() : sortedWindows.get(0);
        // 5. Fallback: if no window satisfies, just take first N seats
        if (bestWindow.isEmpty()) {
            bestWindow = availableSeats.stream().limit(partySize).toList();
        }

        // 6. Convert to SeatAllocation entities and persist
        List<SeatAllocation> allocations = new ArrayList<>();
        for (Seat seat : allPhysicalSeats) {
            if (bestWindow.stream().anyMatch(info -> info.getSeatId().equals(seat.getId()))) {
                SeatAllocation allocation = SeatAllocation.builder()
                        .seat(seat)
                        .journeyDate(journeyDate)
                        .allocatedAt(LocalDateTime.now())
                        .status("BOOKED") // later replace with enum
                        .build();
                allocations.add(allocation);
            }
        }
        return seatAllocationRepository.saveAll(allocations);
    }

    /**
     * Constructs sliding windows of consecutive seats (by seat number ordering) of the required size.
     */
    private List<List<SeatAllocationUtil.SeatInfo>> buildWindows(List<SeatAllocationUtil.SeatInfo> seats, int size) {
        if (seats.size() < size) {
            return Collections.emptyList();
        }
        List<SeatAllocationUtil.SeatInfo> sorted = seats.stream()
                .sorted(Comparator.comparing(SeatAllocationUtil.SeatInfo::getCoachNumber)
                        .thenComparing(SeatAllocationUtil.SeatInfo::getSeatNumber))
                .toList();
        List<List<SeatAllocationUtil.SeatInfo>> windows = new ArrayList<>();
        for (int i = 0; i <= sorted.size() - size; i++) {
            windows.add(sorted.subList(i, i + size));
        }
        return windows;
    }

    /**
     * Scoring algorithm for a candidate window.
     */
    private int scoreWindow(List<SeatAllocationUtil.SeatInfo> window, List<PassengerRequest> passengers) {
        int score = 0;
        // Same coach check
        boolean sameCoach = window.stream()
                .map(SeatAllocationUtil.SeatInfo::getCoachNumber)
                .distinct()
                .count() == 1;
        // Consecutive seat numbers check
        boolean consecutive = true;
        for (int i = 1; i < window.size(); i++) {
            if (!window.get(i).getSeatNumber().equals(window.get(i - 1).getSeatNumber() + 1)) {
                consecutive = false;
                break;
            }
        }
        if (sameCoach && consecutive) {
            score += 100;
        } else if (sameCoach) {
            score += 20; // family grouping
        }
        // Passenger specific preferences
        for (int i = 0; i < Math.min(passengers.size(), window.size()); i++) {
            PassengerRequest p = passengers.get(i);
            SeatAllocationUtil.SeatInfo seatInfo = window.get(i);
            // Berth preference
            if (p.getPreferredBerth() != null && !p.getPreferredBerth().isBlank()) {
                try {
                    BerthType pref = BerthType.valueOf(p.getPreferredBerth().toUpperCase());
                    if (seatInfo.getBerthType() == pref) {
                        score += 50;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            // Quota type (simplified weighting)
            String quota = p.getQuotaType();
            if (quota != null && !quota.isBlank()) {
                switch (quota.toUpperCase()) {
                    case "LADIES" -> score += 20;
                    case "SENIOR_CITIZEN", "SENIOR" -> score += 20;
                    default -> {}
                }
            }
        }
        return score;
    }

    @Override
    public List<SeatAllocation> allocateFamilySeats(Long scheduleId, Long trainId, LocalDate journeyDate, int partySize) {
        // Delegates to weighted allocation without detailed passenger info.
        return allocateSeats(scheduleId, trainId, journeyDate, null, partySize);
    }
}
