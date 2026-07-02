package com.railconnect.user.mapper;

import com.railconnect.entity.Passenger;
import com.railconnect.entity.User;
import com.railconnect.user.dtorequestresponse.PassengerResponse;
import com.railconnect.booking.dtorequestresponse.PassengerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassengerMapper {

    Passenger toEntity(PassengerRequest request);

    @Mapping(target = "passengerId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    PassengerResponse toResponse(Passenger passenger);

    void updatePassengerFromRequest(PassengerRequest request, @MappingTarget Passenger passenger);

    default User map(Long userId) {
        if (userId == null) {
            return null;
        }

        User user = new User();
        user.id = userId;
        return user;
    }
}