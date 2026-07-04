package com.railconnect.train.controller;

import com.railconnect.entity.WaitingListAssignment;
import com.railconnect.train.repository.WaitingListAssignmentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/queues")
public class WaitingListController {

    private final WaitingListAssignmentRepository waitingListAssignmentRepository;

    public WaitingListController(WaitingListAssignmentRepository waitingListAssignmentRepository) {
        this.waitingListAssignmentRepository = waitingListAssignmentRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<List<WaitingListAssignment>> getQueueStatus(
            @RequestParam Long scheduleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {
        
        List<WaitingListAssignment> activeQueue = waitingListAssignmentRepository
                .findByScheduleIdAndJourneyDateAndCurrentStatus(scheduleId, journeyDate, "ACTIVE");
        return ResponseEntity.ok(activeQueue);
    }
}
