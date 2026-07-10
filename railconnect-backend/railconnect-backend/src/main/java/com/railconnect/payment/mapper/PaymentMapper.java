package com.railconnect.payment.mapper;

import com.railconnect.entity.Payment;
import com.railconnect.payment.dtorequestresponse.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "bookingId", source = "booking.id")
    PaymentResponse toResponse(Payment payment);
}
