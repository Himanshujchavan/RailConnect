package com.railconnect.train.service;

import com.railconnect.train.dtorequestresponse.SeatAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;

public interface SeatAvailabilityService {
    List<SeatAvailabilityResponse> getAvailableSeats(Long scheduleId, Long coachId, LocalDate journeyDate);
}