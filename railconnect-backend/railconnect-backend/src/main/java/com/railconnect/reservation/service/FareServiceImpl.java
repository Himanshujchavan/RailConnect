package com.railconnect.reservation.service;

import org.springframework.stereotype.Service;

@Service
public class FareServiceImpl implements FareService {

    @Override
    public double calculateFare(String coachType, int passengerCount) {
        double perPassenger = coachMultiplier(coachType);
        return round2(perPassenger * passengerCount);
    }

    private double coachMultiplier(String coachType) {
        if (coachType == null) {
            return 100.0;
        }

        String normalized = coachType.trim().toLowerCase();
        if (normalized.contains("2 tier") || normalized.contains("second ac")) {
            return 220.0;
        }
        if (normalized.contains("3 tier") || normalized.contains("third ac")) {
            return 170.0;
        }
        if (normalized.contains("chair") || normalized.contains("cc")) {
            return 120.0;
        }
        if (normalized.contains("sleeper")) {
            return 60.0;
        }
        return 100.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}