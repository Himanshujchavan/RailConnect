package com.railconnect.train.service;

import com.railconnect.entity.WaitingListAssignment;
import java.time.LocalDate;

public interface WaitingListService {
    WaitingListAssignment addToQueue(Long scheduleId, LocalDate journeyDate, Long passengerId, int remainingAvailableSeats);
    void processUpgradesOnCancellation(Long scheduleId, LocalDate journeyDate, String vacatedCoachType);
}
