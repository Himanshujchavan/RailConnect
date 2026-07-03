package com.railconnect.user.mapper;

import com.railconnect.entity.Passenger;
import com.railconnect.entity.User;
import com.railconnect.user.dtorequestresponse.UpdateProfileRequest;
import com.railconnect.user.dtorequestresponse.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "passengerIds", source = "passengers")
    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(User user);

    void updateUserFromRequest(UpdateProfileRequest request, @MappingTarget User user);

    default Long map(Passenger passenger) {
        return passenger == null ? null : passenger.id;
    }
}