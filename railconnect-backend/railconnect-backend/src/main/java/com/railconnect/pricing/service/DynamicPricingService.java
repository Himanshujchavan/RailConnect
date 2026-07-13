package com.railconnect.pricing.service;

import com.railconnect.pricing.dtorequestresponse.FareBreakdownResponse;
import com.railconnect.pricing.dtorequestresponse.FareEstimateRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * Dynamic Pricing engine. Layers Weekend / Festival / Peak-Season / Tatkal / Occupancy-based
 * surcharges on top of {@link com.railconnect.reservation.service.FareService}'s flat
 * per-coach-type base rate, then applies per-passenger age concessions (senior discount,
 * infant-free travel). Used both for the actual booking total (Phase 3's
 * {@code BookingOrchestrator}) and for the public fare-estimate endpoint, so a quote shown
 * before booking always matches what gets charged.
 */
public interface DynamicPricingService {

    /**
     * The full breakdown - use this when the caller (or the person booking) should see how the
     * number was reached.
     */
    FareBreakdownResponse estimateFare(FareEstimateRequest request);

    /**
     * Convenience used by the booking flow, where only the final chargeable total is needed.
     */
    double calculateBookingFare(String coachType, LocalDate journeyDate, Long scheduleId, Long coachId,
                                boolean tatkal, List<Integer> passengerAges);
}
