package com.railconnect.pricing.dtorequestresponse;

/**
 * Full transparency breakdown of how a fare was reached, so a passenger never sees a total
 * that looks arbitrary. Multipliers are applied to {@code baseFarePerPassenger} in the order
 * they're listed here (weekend -> festival -> peak season -> tatkal -> occupancy), then the
 * per-passenger age concessions (senior discount / infant-free) are applied last.
 */
public record FareBreakdownResponse(
        double baseFarePerPassenger,
        double weekendMultiplier,
        double festivalMultiplier,
        double peakSeasonMultiplier,
        double tatkalMultiplier,
        double occupancyMultiplier,
        double adjustedFarePerPassenger,
        int passengerCount,
        int seniorCitizenCount,
        int infantCount,
        double totalFare
) {
}
