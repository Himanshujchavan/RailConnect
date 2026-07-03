package com.railconnect.station.controller;

import com.railconnect.station.dtorequestresponse.StationRequest;
import com.railconnect.station.dtorequestresponse.StationResponse;
import com.railconnect.station.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StationController {

    private final StationService service;

    public StationController(StationService service) {
        this.service = service;
    }

    // --- ADMIN ENDPOINTS ---
    @PostMapping("/admin/stations")
    public ResponseEntity<StationResponse> createStation(@Valid @RequestBody StationRequest request) {
        return new ResponseEntity<>(service.createStation(request), HttpStatus.CREATED);
    }

    @PutMapping("/admin/stations/{id}")
    public ResponseEntity<StationResponse> updateStation(@PathVariable Long id, @Valid @RequestBody StationRequest request) {
        return ResponseEntity.ok(service.updateStation(id, request));
    }

    @DeleteMapping("/admin/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        service.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    // --- PUBLIC ENDPOINTS ---
    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> getAllStations() {
        return ResponseEntity.ok(service.getAllStations());
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationResponse> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStationById(id));
    }

    @GetMapping("/stations/search")
    public ResponseEntity<List<StationResponse>> searchStations(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchStations(keyword));
    }
}