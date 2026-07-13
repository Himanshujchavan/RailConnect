package com.railconnect.pricing.mapper;

import com.railconnect.entity.FestivalPricingRule;
import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FestivalPricingRuleMapper {
    FestivalPricingRuleResponse toResponse(FestivalPricingRule rule);
}
