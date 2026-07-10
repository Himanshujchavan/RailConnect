package com.railconnect.payment.mapper;

import com.railconnect.entity.Refund;
import com.railconnect.payment.dtorequestresponse.RefundResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefundMapper {

    @Mapping(target = "refundId", source = "id")
    @Mapping(target = "paymentId", source = "payment.id")
    RefundResponse toResponse(Refund refund);
}
