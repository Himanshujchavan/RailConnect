package com.railconnect.station.service;

import com.railconnect.station.dtorequestresponse.StationRequest;
import com.railconnect.station.dtorequestresponse.StationResponse;
import java.util.List;

public interface StationService {
    StationResponse createStation(StationRequest request);
    List<StationResponse> getAllStations();
    StationResponse getStationById(Long id);
    List<StationResponse> searchStations(String keyword);
    StationResponse updateStation(Long id, StationRequest request);
    void deleteStation(Long id);
}
