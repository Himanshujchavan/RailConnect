package com.railconnect.auth.dtorequestresponse;

import com.railconnect.common.enums.RoleType;

public record LoginResponse(
        Long userId,
        String username,
        String email,
        RoleType role,
        String accessToken,
        String refreshToken
) {
}