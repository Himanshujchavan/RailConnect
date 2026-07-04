package com.railconnect.train.controller;

import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;
import com.railconnect.train.dtorequestresponse.TrainRequest;
import com.railconnect.train.dtorequestresponse.TrainResponse;
import com.railconnect.train.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    // ==========================================
    //          🛡️ ADMIN MANAGEMENT ENDPOINTS
    // ==========================================

    @PostMapping("/admin/trains")
    public ResponseEntity<TrainResponse> createTrain(@Valid @RequestBody TrainRequest request) {
        TrainResponse createdTrain = trainService.createTrain(request);
        return new ResponseEntity<>(createdTrain, HttpStatus.CREATED);
    }

    @PutMapping("/admin/trains/{id}")
    public ResponseEntity<TrainResponse> updateTrain(
            @PathVariable Long id, 
            @Valid @RequestBody TrainRequest request) {
        return ResponseEntity.ok(trainService.updateTrain(id, request));
    }

    @PatchMapping("/admin/trains/{id}/status")
    public ResponseEntity<TrainResponse> updateTrainStatus(
            @PathVariable Long id, 
            @RequestParam TrainStatus status) {
        return ResponseEntity.ok(trainService.updateTrainStatus(id, status));
    }

    @DeleteMapping("/admin/trains/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    //          🌐 PUBLIC INQUIRY ENDPOINTS
    // ==========================================

    @GetMapping("/trains")
    public ResponseEntity<List<TrainResponse>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    @GetMapping("/trains/{id}")
    public ResponseEntity<TrainResponse> getTrainById(@PathVariable Long id) {
        return ResponseEntity.ok(trainService.getTrainById(id));
    }

    @GetMapping("/trains/type/{type}")
    public ResponseEntity<List<TrainResponse>> getTrainsByType(@PathVariable TrainType type) {
        return ResponseEntity.ok(trainService.getTrainsByType(type));
    }
}