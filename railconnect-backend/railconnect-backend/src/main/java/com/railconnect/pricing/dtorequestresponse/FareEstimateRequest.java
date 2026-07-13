package com.railconnect.pricing.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record FareEstimateRequest(
        @NotBlank String coachType,
        @NotNull LocalDate journeyDate,
        @NotNull Long scheduleId,
        @NotNull Long coachId,
        boolean tatkal,

        // One entry per passenger; null or omitted entries are treated as an adult of
        // unknown age (no senior discount, no infant-free travel, no lower-berth priority).
        List<Integer> passengerAges
) {
}
