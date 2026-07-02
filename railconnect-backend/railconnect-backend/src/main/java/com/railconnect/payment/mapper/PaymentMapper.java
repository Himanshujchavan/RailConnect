package com.railconnect.payment.mapper;

import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.payment.dtorequestresponse.PaymentRequest;
import com.railconnect.payment.dtorequestresponse.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "booking", source = "bookingId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "paymentId", source = "id")
    @Mapping(target = "bookingId", source = "booking.id")
    PaymentResponse toResponse(Payment payment);

    default Booking map(Long bookingId) {
        if (bookingId == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.id = bookingId;
        return booking;
    }
}