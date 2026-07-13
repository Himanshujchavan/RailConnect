package com.railconnect.user.dtorequestresponse;

import com.railconnect.common.enums.Gender;

public record PassengerResponse(
        Long passengerId,
        String firstName,
        String lastName,
        Gender gender,
        Integer age,
        Long userId
) {
}