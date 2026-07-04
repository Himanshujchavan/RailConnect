package com.railconnect.train.service;

import com.railconnect.common.enums.BerthType;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Seat;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeatGenerationServiceImpl implements SeatGenerationService {

    private static final BerthType[] TWO_TIER_LAYOUT = {
            BerthType.LOWER,
            BerthType.UPPER,
            BerthType.SIDE_LOWER,
            BerthType.SIDE_UPPER
    };

    private static final BerthType[] THREE_TIER_LAYOUT = {
            BerthType.LOWER,
            BerthType.MIDDLE,
            BerthType.UPPER,
            BerthType.LOWER,
            BerthType.MIDDLE,
            BerthType.UPPER,
            BerthType.SIDE_LOWER,
            BerthType.SIDE_UPPER
    };

    @Override
    public List<Seat> generateSeats(Coach coach) {
        return generateCoachSeats(coach);
    }

    @Override
    public List<Seat> generateCoachSeats(Coach coach) {

        List<Seat> seats = new ArrayList<>();

        for (int seatNumber = 1; seatNumber <= coach.getSeatCount(); seatNumber++) {

            BerthType berthType =
                    generateLayout(coach.getCoachType(), seatNumber);

            seats.add(
                    Seat.builder()
                            .coach(coach)
                            .seatNumber(seatNumber)
                            .berthType(berthType)
                            .seatLabel(
                                    buildSeatLabel(
                                            coach.getCoachNumber(),
                                            seatNumber,
                                            berthType
                                    )
                            )
                            .build()
            );
        }

        return seats;
    }

    @Override
    public BerthType generateLayout(
            String coachType,
            int seatNumber) {

        BerthType[] layout =
                isTwoTierCoach(coachType)
                        ? TWO_TIER_LAYOUT
                        : THREE_TIER_LAYOUT;

        return layout[(seatNumber - 1) % layout.length];
    }

    private boolean isTwoTierCoach(String coachType) {

        return coachType != null &&
                coachType.trim().equalsIgnoreCase("AC 2 Tier");
    }

    private String buildSeatLabel(
            String coachNumber,
            int seatNumber,
            BerthType berthType) {

        String code = switch (berthType) {

            case LOWER -> "LB";
            case MIDDLE -> "MB";
            case UPPER -> "UB";
            case SIDE_LOWER -> "SL";
            case SIDE_UPPER -> "SU";
        };

        return coachNumber + "-" + seatNumber + " " + code;
    }
}