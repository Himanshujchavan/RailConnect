package com.railconnect.search.dtorequestresponse;

import com.railconnect.common.enums.CoachType;

public record FareSearchResponse(
        Long scheduleId,
        String sourceStationCode,
        String destinationStationCode,
        double distanceKm,
        CoachType coachType,
        int passengerAge,
        double fare
) {
}
