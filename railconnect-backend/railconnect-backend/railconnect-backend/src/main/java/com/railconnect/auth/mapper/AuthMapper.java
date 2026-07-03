package com.railconnect.auth.mapper;

import com.railconnect.auth.dtorequestresponse.RegisterRequest;
import com.railconnect.auth.dtorequestresponse.RegisterResponse;
import com.railconnect.auth.dtorequestresponse.UserSummary;
import com.railconnect.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    // username, password and role are deliberately NOT mapped here — the service layer
    // sets username (from email), encodes the password, and looks up the persisted
    // Role entity itself. Letting the mapper touch any of those risks silently creating
    // detached/duplicate rows or storing a plaintext password.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiry", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "fullName", expression = "java(user.fullName())")
    RegisterResponse toRegisterResponse(User user);

    @Mapping(target = "name", expression = "java(user.fullName())")
    @Mapping(target = "role", source = "role.name")
    UserSummary toUserSummary(User user);
}
