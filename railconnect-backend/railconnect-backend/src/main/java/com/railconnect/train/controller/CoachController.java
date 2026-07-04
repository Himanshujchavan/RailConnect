package com.railconnect.train.controller;

import com.railconnect.journey.dtorequestresponse.CoachRequest;
import com.railconnect.journey.dtorequestresponse.CoachResponse;
import com.railconnect.train.service.CoachService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CoachController {

    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    @PostMapping("/admin/coaches")
    public ResponseEntity<CoachResponse> addCoachToTrain(@Valid @RequestBody CoachRequest request) {
        CoachResponse createdCoach = coachService.addCoachToTrain(request);
        return new ResponseEntity<>(createdCoach, HttpStatus.CREATED);
    }

    @GetMapping("/coaches/train/{trainId}")
    public ResponseEntity<List<CoachResponse>> getCoachesByTrain(@PathVariable Long trainId) {
        return ResponseEntity.ok(coachService.getCoachesByTrain(trainId));
    }

    @DeleteMapping("/admin/coaches/{id}")
    public ResponseEntity<Void> removeCoach(@PathVariable Long id) {
        coachService.removeCoach(id);
        return ResponseEntity.noContent().build();
    }
}
