package com.railconnect.journey.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RouteRequest {

    @NotBlank(message = "Route name is required")
    private String routeName;

    @NotNull(message = "Source station ID is required")
    private Long sourceStationId;

    @NotNull(message = "Destination station ID is required")
    private Long destinationStationId;

    @NotEmpty(message = "Route must contain at least the source and destination stops")
    private List<RouteStationRequest> routeStations;
}