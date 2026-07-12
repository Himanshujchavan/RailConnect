package com.railconnect.report.dtorequestresponse;

public record CoachUtilizationResponse(
        String coachType,
        long bookedSeatDays,
        long totalSeatCapacity,
        double utilizationPercent
) {
}
