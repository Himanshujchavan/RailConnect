package com.railconnect.pricing.mapper;

import com.railconnect.entity.PeakSeasonPricingRule;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PeakSeasonPricingRuleMapper {
    PeakSeasonPricingRuleResponse toResponse(PeakSeasonPricingRule rule);
}
