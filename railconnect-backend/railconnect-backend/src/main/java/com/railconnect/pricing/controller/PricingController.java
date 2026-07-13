package com.railconnect.pricing.controller;

import com.railconnect.pricing.dtorequestresponse.FareBreakdownResponse;
import com.railconnect.pricing.dtorequestresponse.FareEstimateRequest;
import com.railconnect.pricing.service.DynamicPricingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public-facing Dynamic Pricing endpoint - shows a passenger the full fare breakdown (including
 * any weekend/festival/peak-season/tatkal/occupancy surcharges and age concessions) before they
 * commit to a booking. Uses the exact same engine as the booking flow, so this quote and the
 * eventual charge never disagree.
 */
@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final DynamicPricingService dynamicPricingService;

    public PricingController(DynamicPricingService dynamicPricingService) {
        this.dynamicPricingService = dynamicPricingService;
    }

    @PostMapping("/estimate")
    public ResponseEntity<FareBreakdownResponse> estimateFare(@Valid @RequestBody FareEstimateRequest request) {
        return ResponseEntity.ok(dynamicPricingService.estimateFare(request));
    }
}
