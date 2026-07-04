package com.railconnect.reservation.service;

import com.railconnect.entity.SeatAllocation;
import java.time.LocalDate;
import java.util.List;

public interface SeatAllocationService {

    List<SeatAllocation> allocateSeats(
            Long scheduleId,
            Long trainId,
            LocalDate journeyDate,
            String berthPreference,
            int passengerCount);

    List<SeatAllocation> allocateFamilySeats(
            Long scheduleId,
            Long trainId,
            LocalDate journeyDate,
            int partySize);
}
