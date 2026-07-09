package com.railconnect.reservation.service;

import com.railconnect.entity.SeatAllocation;
import java.time.LocalDate;
import java.util.List;

public interface SeatAllocationService {

    /**
     * Allocates seats within the given coach, honoring the berth preference where possible
     * and falling back to any available seat otherwise.
     */
    List<SeatAllocation> allocateSeats(
            Long scheduleId,
            Long coachId,
            LocalDate journeyDate,
            String berthPreference,
            int passengerCount);

    /**
     * Allocates seats within the given coach for a family/group, preferring a contiguous
     * block of seat numbers so the party can sit together.
     */
    List<SeatAllocation> allocateFamilySeats(
            Long scheduleId,
            Long coachId,
            LocalDate journeyDate,
            int partySize);
}
