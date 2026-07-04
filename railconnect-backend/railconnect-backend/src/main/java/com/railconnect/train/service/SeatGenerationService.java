package com.railconnect.train.service;

import com.railconnect.common.enums.BerthType;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Seat;

import java.util.List;

public interface SeatGenerationService {

    List<Seat> generateSeats(Coach coach);

    List<Seat> generateCoachSeats(Coach coach);

    BerthType generateLayout(String coachType, int seatNumber);
}