package com.railconnect.pricing.controller;

import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleRequest;
import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleResponse;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleRequest;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleResponse;
import com.railconnect.pricing.service.PricingAdminService;
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
 * Phase 8-style admin management of Dynamic Pricing rules.
 */
@RestController
@RequestMapping("/api/v1/admin/pricing")
public class PricingAdminController {

    private final PricingAdminService pricingAdminService;

    public PricingAdminController(PricingAdminService pricingAdminService) {
        this.pricingAdminService = pricingAdminService;
    }

    // --- Festival Pricing (one rule per date - create/update is an upsert) ---

    @GetMapping("/festivals")
    public ResponseEntity<List<FestivalPricingRuleResponse>> getAllFestivalRules() {
        return ResponseEntity.ok(pricingAdminService.getAllFestivalRules());
    }

    @PostMapping("/festivals")
    public ResponseEntity<FestivalPricingRuleResponse> upsertFestivalRule(
            @Valid @RequestBody FestivalPricingRuleRequest request) {
        return new ResponseEntity<>(pricingAdminService.upsertFestivalRule(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/festivals/{id}")
    public ResponseEntity<Void> deleteFestivalRule(@PathVariable Long id) {
        pricingAdminService.deleteFestivalRule(id);
        return ResponseEntity.noContent().build();
    }

    // --- Peak Season Pricing ---

    @GetMapping("/peak-seasons")
    public ResponseEntity<List<PeakSeasonPricingRuleResponse>> getAllPeakSeasonRules() {
        return ResponseEntity.ok(pricingAdminService.getAllPeakSeasonRules());
    }

    @PostMapping("/peak-seasons")
    public ResponseEntity<PeakSeasonPricingRuleResponse> createPeakSeasonRule(
            @Valid @RequestBody PeakSeasonPricingRuleRequest request) {
        return new ResponseEntity<>(pricingAdminService.createPeakSeasonRule(request), HttpStatus.CREATED);
    }

    @PutMapping("/peak-seasons/{id}")
    public ResponseEntity<PeakSeasonPricingRuleResponse> updatePeakSeasonRule(
            @PathVariable Long id, @Valid @RequestBody PeakSeasonPricingRuleRequest request) {
        return ResponseEntity.ok(pricingAdminService.updatePeakSeasonRule(id, request));
    }

    @DeleteMapping("/peak-seasons/{id}")
    public ResponseEntity<Void> deletePeakSeasonRule(@PathVariable Long id) {
        pricingAdminService.deletePeakSeasonRule(id);
        return ResponseEntity.noContent().build();
    }
}
