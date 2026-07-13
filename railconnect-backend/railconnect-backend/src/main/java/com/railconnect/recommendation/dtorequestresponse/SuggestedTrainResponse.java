package com.railconnect.recommendation.dtorequestresponse;

import java.time.LocalTime;

public record SuggestedTrainResponse(
        Long trainId,
        String trainNumber,
        String trainName,
        Long scheduleId,
        Long routeId,
        LocalTime departureTime,
        LocalTime arrivalTime
) {
}
