package com.railconnect.auth.dtorequestresponse;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserSummary user
) {
}
