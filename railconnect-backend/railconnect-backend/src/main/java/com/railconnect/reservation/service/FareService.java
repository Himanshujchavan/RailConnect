package com.railconnect.reservation.service;

public interface FareService {
    double calculateFare(String coachType, int passengerCount);

    /**
     * The flat base rate for a single passenger in this coach type, before any Dynamic
     * Pricing surcharges/discounts (weekend, festival, peak season, tatkal, occupancy, age)
     * are layered on top by {@link com.railconnect.pricing.service.DynamicPricingService}.
     */
    double baseRatePerPassenger(String coachType);
}