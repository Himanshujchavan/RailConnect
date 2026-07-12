package com.railconnect.booking.mapper;

import com.railconnect.booking.dtorequestresponse.BookingHistoryResponse;
import com.railconnect.booking.dtorequestresponse.BookingResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.booking.dtorequestresponse.PassengerRequest;
import com.railconnect.entity.Booking;
import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.Passenger;
import com.railconnect.entity.Payment;
import com.railconnect.entity.PNR;
import com.railconnect.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "bookingPassengers", source = "passengers")

    Booking toEntity(CreateBookingRequest request);

    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "passenger", source = ".")
    BookingPassenger toBookingPassenger(PassengerRequest request);

    @Mapping(target = "id", ignore = true)
    Passenger toPassenger(PassengerRequest request);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookingPassengerIds", source = "bookingPassengers")
    BookingResponse toResponse(Booking booking);

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "pnrId", source = "pnr.id")
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "status", source = "booking.status")
    @Mapping(target = "createdAt", source = "booking.createdAt") 
    @Mapping(target = "passengerCount", expression = "java(booking.bookingPassengers == null ? 0 : booking.bookingPassengers.size())")
    BookingHistoryResponse toHistoryResponse(Booking booking, PNR pnr, Payment payment);

    default User map(Long userId) {
        if (userId == null) {
            return null;
        }

        User user = new User();
        user.id = userId;
        return user;
    }

    default Long map(BookingPassenger bookingPassenger) {
        return bookingPassenger == null ? null : bookingPassenger.id;
    }
}