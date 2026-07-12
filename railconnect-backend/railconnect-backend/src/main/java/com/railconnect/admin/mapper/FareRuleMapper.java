package com.railconnect.admin.mapper;

import com.railconnect.admin.dtorequestresponse.FareRuleResponse;
import com.railconnect.entity.FareRule;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FareRuleMapper {
    FareRuleResponse toResponse(FareRule fareRule);
}
