package com.railconnect.admin.dtorequestresponse;

import com.railconnect.common.enums.CoachType;

import java.time.LocalDateTime;

public record FareRuleResponse(
        Long id,
        CoachType coachType,
        Double ratePerKm,
        LocalDateTime updatedAt
) {
}
