package com.railconnect.pricing.dtorequestresponse;

import java.time.LocalDate;

public record PeakSeasonPricingRuleResponse(
        Long id,
        String seasonName,
        LocalDate startDate,
        LocalDate endDate,
        Double surchargePercent
) {
}
