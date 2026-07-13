package com.railconnect.pricing.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record FestivalPricingRuleRequest(
        @NotNull LocalDate date,
        @NotBlank String festivalName,
        @NotNull @Positive Double surchargePercent
) {
}
