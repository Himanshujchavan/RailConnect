package com.railconnect.pricing.service;

import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleRequest;
import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleResponse;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleRequest;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleResponse;

import java.util.List;

/**
 * Phase 8-style admin CRUD for the Dynamic Pricing rule tables. Kept separate from
 * {@link DynamicPricingService} (the read-heavy calculation engine) so the two responsibilities
 * - "manage the rules" vs. "apply the rules" - don't get tangled into one class.
 */
public interface PricingAdminService {

    List<FestivalPricingRuleResponse> getAllFestivalRules();
    FestivalPricingRuleResponse upsertFestivalRule(FestivalPricingRuleRequest request);
    void deleteFestivalRule(Long id);

    List<PeakSeasonPricingRuleResponse> getAllPeakSeasonRules();
    PeakSeasonPricingRuleResponse createPeakSeasonRule(PeakSeasonPricingRuleRequest request);
    PeakSeasonPricingRuleResponse updatePeakSeasonRule(Long id, PeakSeasonPricingRuleRequest request);
    void deletePeakSeasonRule(Long id);
}
