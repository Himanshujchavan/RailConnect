package com.railconnect.pricing.dtorequestresponse;

import java.time.LocalDate;

public record FestivalPricingRuleResponse(
        Long id,
        LocalDate date,
        String festivalName,
        Double surchargePercent
) {
}
