package com.railconnect.station.dtorequestresponse;

import lombok.Data;

@Data
public class StationResponse {
    private Long id;
    private String stationCode;
    private String stationName;
    private String city;
    private String state;
}