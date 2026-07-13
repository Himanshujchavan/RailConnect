package com.railconnect.report.dtorequestresponse;

public record RouteRevenueResponse(
        Long routeId,
        String routeName,
        String sourceStationCode,
        String destinationStationCode,
        double netRevenue
) {
}
