package com.railconnect.journey.dtorequestresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStationResponse {
    private Long id;
    private Long stationId;
    private String stationCode;
    private String stationName;
    private Integer stopOrder;
    private Integer distanceFromSource;
}