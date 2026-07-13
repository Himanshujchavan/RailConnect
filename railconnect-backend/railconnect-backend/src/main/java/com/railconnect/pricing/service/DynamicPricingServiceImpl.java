package com.railconnect.pricing.service;

import com.railconnect.common.exception.InvalidRequestException;
import com.railconnect.entity.Coach;
import com.railconnect.entity.PeakSeasonPricingRule;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.pricing.dtorequestresponse.FareBreakdownResponse;
import com.railconnect.pricing.dtorequestresponse.FareEstimateRequest;
import com.railconnect.pricing.repository.FestivalPricingRuleRepository;
import com.railconnect.pricing.repository.PeakSeasonPricingRuleRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.reservation.service.FareService;
import com.railconnect.train.repository.CoachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sole implementation of {@link DynamicPricingService}.
 * <p>
 * Multipliers are applied to the per-passenger base rate in this order: weekend -> festival ->
 * peak season -> tatkal -> occupancy. Per-passenger age concessions (senior discount,
 * infant-free) are applied last, individually per passenger.
 */
@Service
public class DynamicPricingServiceImpl implements DynamicPricingService {

    private static final double WEEKEND_MULTIPLIER = 1.15;
    private static final double TATKAL_MULTIPLIER = 1.30;
    private static final int TATKAL_WINDOW_DAYS = 1;

    private static final double OCCUPANCY_HIGH_THRESHOLD = 0.95;
    private static final double OCCUPANCY_HIGH_MULTIPLIER = 1.35;
    private static final double OCCUPANCY_ELEVATED_THRESHOLD = 0.80;
    private static final double OCCUPANCY_ELEVATED_MULTIPLIER = 1.20;
    private static final double OCCUPANCY_MODERATE_THRESHOLD = 0.50;
    private static final double OCCUPANCY_MODERATE_MULTIPLIER = 1.10;

    private static final int SENIOR_AGE_THRESHOLD = 60;
    private static final int INFANT_AGE_LIMIT = 5;
    private static final double SENIOR_DISCOUNT_MULTIPLIER = 0.60;

    private final FareService fareService;
    private final FestivalPricingRuleRepository festivalPricingRuleRepository;
    private final PeakSeasonPricingRuleRepository peakSeasonPricingRuleRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final CoachRepository coachRepository;

    public DynamicPricingServiceImpl(FareService fareService,
                                      FestivalPricingRuleRepository festivalPricingRuleRepository,
                                      PeakSeasonPricingRuleRepository peakSeasonPricingRuleRepository,
                                      SeatAllocationRepository seatAllocationRepository,
                                      CoachRepository coachRepository) {
        this.fareService = fareService;
        this.festivalPricingRuleRepository = festivalPricingRuleRepository;
        this.peakSeasonPricingRuleRepository = peakSeasonPricingRuleRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FareBreakdownResponse estimateFare(FareEstimateRequest request) {
        List<Integer> ages = request.passengerAges() != null && !request.passengerAges().isEmpty()
                ? request.passengerAges()
                : Collections.singletonList(null);

        return buildBreakdown(request.coachType(), request.journeyDate(), request.scheduleId(),
                request.coachId(), request.tatkal(), ages);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateBookingFare(String coachType, LocalDate journeyDate, Long scheduleId, Long coachId,
                                        boolean tatkal, List<Integer> passengerAges) {
        List<Integer> ages = passengerAges != null && !passengerAges.isEmpty()
                ? passengerAges
                : Collections.singletonList(null);

        return buildBreakdown(coachType, journeyDate, scheduleId, coachId, tatkal, ages).totalFare();
    }

    private FareBreakdownResponse buildBreakdown(String coachType, LocalDate journeyDate, Long scheduleId,
                                                  Long coachId, boolean tatkal, List<Integer> ages) {
        double baseFare = fareService.baseRatePerPassenger(coachType);

        double weekendMultiplier = isWeekend(journeyDate) ? WEEKEND_MULTIPLIER : 1.0;
        double festivalMultiplier = festivalMultiplier(journeyDate);
        double peakSeasonMultiplier = peakSeasonMultiplier(journeyDate);
        double tatkalMultiplier = tatkalMultiplier(journeyDate, tatkal);
        double occupancyMultiplier = occupancyMultiplier(scheduleId, coachId, journeyDate);

        double adjustedFare = baseFare * weekendMultiplier * festivalMultiplier
                * peakSeasonMultiplier * tatkalMultiplier * occupancyMultiplier;

        double total = 0.0;
        int seniorCount = 0;
        int infantCount = 0;

        for (Integer age : ages) {
            if (age != null && age >= SENIOR_AGE_THRESHOLD) {
                total += adjustedFare * SENIOR_DISCOUNT_MULTIPLIER;
                seniorCount++;
            } else if (age != null && age < INFANT_AGE_LIMIT) {
                infantCount++;
                // Infants travel free - contributes 0 to the total.
            } else {
                total += adjustedFare;
            }
        }

        return new FareBreakdownResponse(
                round2(baseFare),
                weekendMultiplier,
                festivalMultiplier,
                peakSeasonMultiplier,
                tatkalMultiplier,
                occupancyMultiplier,
                round2(adjustedFare),
                ages.size(),
                seniorCount,
                infantCount,
                round2(total));
    }

    private boolean isWeekend(LocalDate journeyDate) {
        DayOfWeek day = journeyDate.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private double festivalMultiplier(LocalDate journeyDate) {
        return festivalPricingRuleRepository.findByDate(journeyDate)
                .map(rule -> rule.surchargePercent != null ? rule.surchargePercent : 0.0)
                .map(percent -> 1.0 + percent / 100.0)
                .orElse(1.0);
    }

    private double peakSeasonMultiplier(LocalDate journeyDate) {
        List<PeakSeasonPricingRule> active = peakSeasonPricingRuleRepository.findActiveOn(journeyDate);
        double maxPercent = active.stream()
                .map(rule -> rule.surchargePercent != null ? rule.surchargePercent : 0.0)
                .max(Double::compareTo)
                .orElse(0.0);
        return 1.0 + maxPercent / 100.0;
    }

    private double tatkalMultiplier(LocalDate journeyDate, boolean tatkal) {
        if (!tatkal) {
            return 1.0;
        }
        long daysUntilJourney = LocalDate.now().until(journeyDate).getDays();
        boolean withinWindow = !journeyDate.isBefore(LocalDate.now())
                && daysUntilJourney <= TATKAL_WINDOW_DAYS;
        if (!withinWindow) {
            throw new InvalidRequestException(
                    "Tatkal pricing is only available within " + TATKAL_WINDOW_DAYS
                            + " day(s) of the journey date");
        }
        return TATKAL_MULTIPLIER;
    }

    /**
     * Occupancy-based Pricing: the fuller a coach already is for this schedule/date, the higher
     * the surcharge on the seats still left in it.
     */
    private double occupancyMultiplier(Long scheduleId, Long coachId, LocalDate journeyDate) {
        Coach coach = coachRepository.findById(coachId).orElse(null);
        if (coach == null || coach.getSeatCount() == null || coach.getSeatCount() == 0) {
            return 1.0;
        }

        Set<Long> coachSeatIds = coach.getSeats().stream().map(seat -> seat.getId()).collect(Collectors.toSet());
        long occupied = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate).stream()
                .map(SeatAllocation::getSeat)
                .filter(seat -> coachSeatIds.contains(seat.getId()))
                .count();

        double ratio = occupied / (double) coach.getSeatCount();

        if (ratio >= OCCUPANCY_HIGH_THRESHOLD) {
            return OCCUPANCY_HIGH_MULTIPLIER;
        }
        if (ratio >= OCCUPANCY_ELEVATED_THRESHOLD) {
            return OCCUPANCY_ELEVATED_MULTIPLIER;
        }
        if (ratio >= OCCUPANCY_MODERATE_THRESHOLD) {
            return OCCUPANCY_MODERATE_MULTIPLIER;
        }
        return 1.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
