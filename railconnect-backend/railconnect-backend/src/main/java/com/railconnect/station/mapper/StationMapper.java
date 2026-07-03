package com.railconnect.station.mapper;

import com.railconnect.station.dtorequestresponse.StationRequest;
import com.railconnect.station.dtorequestresponse.StationResponse;
import com.railconnect.station.entity.Station;
import org.springframework.stereotype.Component;

@Component
public class StationMapper {

    public Station toEntity(StationRequest request) {
        if (request == null) return null;
        return Station.builder()
                .stationCode(request.getStationCode().toUpperCase())
                .stationName(request.getStationName())
                .city(request.getCity())
                .state(request.getState())
                .build();
    }

    public StationResponse toResponse(Station station) {
        if (station == null) return null;
        StationResponse response = new StationResponse();
        response.setId(station.getId());
        response.setStationCode(station.getStationCode());
        response.setStationName(station.getStationName());
        response.setCity(station.getCity());
        response.setState(station.getState());
        return response;
    }
}
