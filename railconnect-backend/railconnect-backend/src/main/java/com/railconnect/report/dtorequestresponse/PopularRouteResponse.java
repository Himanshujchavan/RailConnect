package com.railconnect.report.dtorequestresponse;

public record PopularRouteResponse(
        Long routeId,
        String routeName,
        String sourceStationCode,
        String destinationStationCode,
        long bookingCount
) {
}
