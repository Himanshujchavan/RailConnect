package com.railconnect.reservation.service;

import java.time.LocalDate;
import java.util.List;

public interface SeatAllocationService {
    List<Long> allocateSeats(Long scheduleId, Long coachId, LocalDate journeyDate, String preference, int passengerCount);
    void releaseSeat(Long seatId, Long scheduleId, LocalDate journeyDate);
}