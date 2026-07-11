package com.railconnect.search.service;

import com.railconnect.common.enums.CoachType;
import com.railconnect.journey.dtorequestresponse.RouteResponse;
import com.railconnect.search.dtorequestresponse.CoachAvailabilityResponse;
import com.railconnect.search.dtorequestresponse.FareSearchResponse;
import com.railconnect.station.dtorequestresponse.StationResponse;
import com.railconnect.train.dtorequestresponse.TrainSearchResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Phase 6 — Search Engine.
 * <p>
 * A thin, read-only facade over Train/Route/Station/Availability/Fare data. Deliberately doesn't
 * duplicate business logic already owned by other modules (e.g. per-seat availability lives in
 * {@link com.railconnect.train.service.SeatAvailabilityService}) - it composes their
 * repositories/services into the cross-cutting queries a "search" page actually needs.
 */
public interface SearchService {

    List<TrainSearchResponse> searchTrains(String sourceStationCode, String destinationStationCode, LocalDate journeyDate);

    List<RouteResponse> searchRoutes(String sourceStationCode, String destinationStationCode);

    List<StationResponse> searchStations(String keyword);

    List<CoachAvailabilityResponse> searchAvailability(Long scheduleId, LocalDate journeyDate);

    FareSearchResponse searchFare(Long scheduleId, String sourceStationCode, String destinationStationCode,
                                  CoachType coachType, int passengerAge);
}
