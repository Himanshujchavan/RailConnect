package com.railconnect.admin.service;

import com.railconnect.admin.dtorequestresponse.FareRuleRequest;
import com.railconnect.admin.dtorequestresponse.FareRuleResponse;
import com.railconnect.common.enums.CoachType;

import java.util.List;

/**
 * Phase 8 — Fare Management.
 * <p>
 * Lets admins override the per-km rate {@link com.railconnect.common.util.FareCalculator} uses
 * for a coach type. Search's fare-estimate endpoint ({@code GET /api/v1/search/fare}) reads
 * through {@link #getEffectiveRatePerKm(CoachType)}, so a change here takes effect immediately
 * without a deploy.
 */
public interface FareManagementService {
    List<FareRuleResponse> getAllFareRules();
    FareRuleResponse upsertFareRule(FareRuleRequest request);
    void deleteFareRule(CoachType coachType);

    /**
     * The rate to actually charge for this coach type: an admin override if one exists,
     * otherwise {@code null} (caller should fall back to FareCalculator's built-in default).
     */
    Double getEffectiveRatePerKm(CoachType coachType);
}
