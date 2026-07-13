package com.railconnect.pricing.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PeakSeasonPricingRuleRequest(
        @NotBlank String seasonName,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @Positive Double surchargePercent
) {
}
