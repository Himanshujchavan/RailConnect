package com.railconnect.admin.controller;

import com.railconnect.admin.dtorequestresponse.FareRuleRequest;
import com.railconnect.admin.dtorequestresponse.FareRuleResponse;
import com.railconnect.admin.service.FareManagementService;
import com.railconnect.common.enums.CoachType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Phase 8 — Admin Panel: Fare Management.
 * <p>
 * {@code coachType} doubles as the identifier here (one rule per coach type), so create and
 * update are the same upsert operation - simpler for admins than juggling a separate rule id.
 */
@RestController
@RequestMapping("/api/v1/admin/fares")
public class FareManagementController {

    private final FareManagementService fareManagementService;

    public FareManagementController(FareManagementService fareManagementService) {
        this.fareManagementService = fareManagementService;
    }

    @GetMapping
    public ResponseEntity<List<FareRuleResponse>> getAllFareRules() {
        return ResponseEntity.ok(fareManagementService.getAllFareRules());
    }

    @PostMapping
    public ResponseEntity<FareRuleResponse> createFareRule(@Valid @RequestBody FareRuleRequest request) {
        return new ResponseEntity<>(fareManagementService.upsertFareRule(request), HttpStatus.CREATED);
    }

    @PutMapping("/{coachType}")
    public ResponseEntity<FareRuleResponse> updateFareRule(@PathVariable CoachType coachType,
                                                            @Valid @RequestBody FareRuleRequest request) {
        if (request.coachType() != coachType) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(fareManagementService.upsertFareRule(request));
    }

    @DeleteMapping("/{coachType}")
    public ResponseEntity<Void> deleteFareRule(@PathVariable CoachType coachType) {
        fareManagementService.deleteFareRule(coachType);
        return ResponseEntity.noContent().build();
    }
}
