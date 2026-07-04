package com.railconnect.train.controller;

import com.railconnect.journey.dtorequestresponse.ScheduleRequest;
import com.railconnect.journey.dtorequestresponse.ScheduleResponse;
import com.railconnect.train.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // ==========================================
    //          🛡️ ADMIN TIMETABLE ENDPOINTS
    // ==========================================

    @PostMapping("/admin/schedules")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        ScheduleResponse createdSchedule = scheduleService.createSchedule(request);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    //          🌐 PUBLIC TIMETABLE ENDPOINTS
    // ==========================================

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/schedules/train/{trainId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByTrainId(@PathVariable Long trainId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByTrainId(trainId));
    }
}
