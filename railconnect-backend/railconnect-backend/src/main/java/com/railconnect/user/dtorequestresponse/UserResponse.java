package com.railconnect.user.dtorequestresponse;

import com.railconnect.common.enums.RoleType;

import java.util.List;

public record UserResponse(
        Long userId,
        String username,
        String email,
        RoleType role,
        List<Long> passengerIds
) {
}