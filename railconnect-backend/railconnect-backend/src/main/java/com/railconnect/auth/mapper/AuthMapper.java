package com.railconnect.auth.mapper;

import com.railconnect.auth.dtorequestresponse.LoginResponse;
import com.railconnect.auth.dtorequestresponse.RegisterRequest;
import com.railconnect.auth.dtorequestresponse.RegisterResponse;
import com.railconnect.common.enums.RoleType;
import com.railconnect.entity.Role;
import com.railconnect.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "role", source = "role")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passengers", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "userId", source = "id")
    LoginResponse toLoginResponse(User user, String accessToken, String refreshToken);

    @Mapping(target = "userId", source = "id")
    RegisterResponse toRegisterResponse(User user, String message);

    default Role map(RoleType roleType) {
        if (roleType == null) {
            return null;
        }

        Role role = new Role();
        role.name = roleType;
        return role;
    }
}