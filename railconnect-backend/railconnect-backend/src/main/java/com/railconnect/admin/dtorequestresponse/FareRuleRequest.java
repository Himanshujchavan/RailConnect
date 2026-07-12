package com.railconnect.admin.dtorequestresponse;

import com.railconnect.common.enums.CoachType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FareRuleRequest(
        @NotNull CoachType coachType,
        @NotNull @Positive Double ratePerKm
) {
}
