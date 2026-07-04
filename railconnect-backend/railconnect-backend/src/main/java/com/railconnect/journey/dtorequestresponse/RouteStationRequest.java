package com.railconnect.journey.dtorequestresponse;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteStationRequest {

    @NotNull(message = "Station ID is required")
    private Long stationId;

    @NotNull(message = "Stop order is required")
    private Integer stopOrder;

    @NotNull(message = "Distance from source is required")
    private Integer distanceFromSource;
}
