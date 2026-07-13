package com.railconnect.recommendation.dtorequestresponse;

import java.time.LocalDate;

public record FrequentRouteResponse(
        Long routeId,
        String routeName,
        String sourceStationCode,
        String destinationStationCode,
        long bookingCount,
        LocalDate lastTravelled
) {
}
