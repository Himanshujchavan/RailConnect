package com.railconnect.search.controller;

import com.railconnect.common.enums.CoachType;
import com.railconnect.journey.dtorequestresponse.RouteResponse;
import com.railconnect.search.dtorequestresponse.CoachAvailabilityResponse;
import com.railconnect.search.dtorequestresponse.FareSearchResponse;
import com.railconnect.search.service.SearchService;
import com.railconnect.station.dtorequestresponse.StationResponse;
import com.railconnect.train.dtorequestresponse.TrainSearchResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Phase 6 — Search Engine.
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/trains")
    public ResponseEntity<List<TrainSearchResponse>> searchTrains(
            @RequestParam String sourceStationCode,
            @RequestParam String destinationStationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {
        return ResponseEntity.ok(searchService.searchTrains(sourceStationCode, destinationStationCode, journeyDate));
    }

    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> searchRoutes(
            @RequestParam String sourceStationCode,
            @RequestParam String destinationStationCode) {
        return ResponseEntity.ok(searchService.searchRoutes(sourceStationCode, destinationStationCode));
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> searchStations(@RequestParam String keyword) {
        return ResponseEntity.ok(searchService.searchStations(keyword));
    }

    @GetMapping("/availability")
    public ResponseEntity<List<CoachAvailabilityResponse>> searchAvailability(
            @RequestParam Long scheduleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {
        return ResponseEntity.ok(searchService.searchAvailability(scheduleId, journeyDate));
    }

    @GetMapping("/fare")
    public ResponseEntity<FareSearchResponse> searchFare(
            @RequestParam Long scheduleId,
            @RequestParam String sourceStationCode,
            @RequestParam String destinationStationCode,
            @RequestParam CoachType coachType,
            @RequestParam int passengerAge) {
        return ResponseEntity.ok(searchService.searchFare(scheduleId, sourceStationCode, destinationStationCode,
                coachType, passengerAge));
    }
}
