package com.railconnect.auth.dtorequestresponse;

import com.railconnect.common.enums.RoleType;

public record UserSummary(
        Long id,
        String name,
        RoleType role
) {
}
