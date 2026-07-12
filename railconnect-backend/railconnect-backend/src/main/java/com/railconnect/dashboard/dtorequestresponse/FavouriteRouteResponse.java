package com.railconnect.dashboard.dtorequestresponse;

import java.time.LocalDateTime;

public record FavouriteRouteResponse(
        Long favouriteRouteId,
        Long routeId,
        String routeName,
        String sourceStationCode,
        String destinationStationCode,
        LocalDateTime createdAt
) {
}
