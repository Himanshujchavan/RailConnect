package com.railconnect.dashboard.dtorequestresponse;

import jakarta.validation.constraints.NotNull;

public record FavouriteRouteRequest(
        @NotNull Long routeId
) {
}
