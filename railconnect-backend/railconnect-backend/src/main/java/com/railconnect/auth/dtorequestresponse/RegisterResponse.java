package com.railconnect.auth.dtorequestresponse;

public record RegisterResponse(
        Long userId,
        String fullName,
        String email
) {
}