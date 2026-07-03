package com.railconnect.common.util;

import com.railconnect.common.enums.CoachType;

public final class FareCalculator {

    private FareCalculator() {
        // Prevent instantiation
    }

    /**
     * Calculates ticket fare dynamically based on distance, coach type, and age logic.
     */
    public static double calculateFare(double distanceInKm, CoachType coachType, int passengerAge) {
        if (distanceInKm <= 0) {
            return 0.0;
        }

        // Base rate calculation per kilometer covering every single CoachType enum constant
        double ratePerKm = switch (coachType) {
            case FIRST_AC -> 3.50;
            case SECOND_AC -> 2.20;
            case THIRD_AC -> 1.70;
            case AC_CHAIR_CAR -> 1.20;
            case SLEEPER -> 0.60;
            case SECOND_SITTING -> 0.40;
            case GENERAL -> 0.25;
        };

        double baseFare = distanceInKm * ratePerKm;

        // Apply concessions based on passenger profile
        if (passengerAge >= 60) {
            baseFare = baseFare * 0.60; // 40% discount for senior citizens
        } else if (passengerAge < 5) {
            baseFare = baseFare * 0.0;  // Free travel for infants
        }

        return Math.round(baseFare * 100.0) / 100.0; // Standard currency rounding
    }
}