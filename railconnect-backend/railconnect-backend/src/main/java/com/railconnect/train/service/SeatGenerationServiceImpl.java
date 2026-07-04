package com.railconnect.train.service;

import com.railconnect.entity.Coach;
import com.railconnect.entity.Seat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatGenerationServiceImpl implements SeatGenerationService {

    private static final String[] TWO_TIER_LAYOUT = {"LOWER", "UPPER", "SIDE LOWER", "SIDE UPPER"};
    private static final String[] THREE_TIER_LAYOUT = {
            "LOWER", "MIDDLE", "UPPER", "LOWER", "MIDDLE", "UPPER", "SIDE LOWER", "SIDE UPPER"
    };

    @Override
    public List<Seat> generateSeats(Coach coach) {
        return generateCoachSeats(coach);
    }

    @Override
    public List<Seat> generateCoachSeats(Coach coach) {
        List<Seat> seats = new ArrayList<>();
        for (int seatNumber = 1; seatNumber <= coach.getSeatCount(); seatNumber++) {
            String berthType = generateLayout(coach.getCoachType(), seatNumber);
            seats.add(Seat.builder()
                    .coach(coach)
                    .seatNumber(seatNumber)
                    .berthType(berthType)
                    .seatLabel(buildSeatLabel(coach.getCoachNumber(), seatNumber, berthType))
                    .build());
        }
        return seats;
    }

    @Override
    public String generateLayout(String coachType, int seatNumber) {
        String[] layout = isTwoTierCoach(coachType) ? TWO_TIER_LAYOUT : THREE_TIER_LAYOUT;
        return layout[(seatNumber - 1) % layout.length];
    }

    private boolean isTwoTierCoach(String coachType) {
        return coachType != null && coachType.trim().equalsIgnoreCase("AC 2 Tier");
    }

    private String buildSeatLabel(String coachNumber, int seatNumber, String berthType) {
        String normalizedBerthType = berthType.replace(" ", "");
        return coachNumber + "-" + seatNumber + normalizedBerthType;
    }
}