package com.railconnect.auth.dtorequestresponse;

import com.railconnect.common.enums.RoleType;

public record RegisterResponse(
        Long userId,
        String username,
        String email,
        RoleType role,
        String message
) {
}