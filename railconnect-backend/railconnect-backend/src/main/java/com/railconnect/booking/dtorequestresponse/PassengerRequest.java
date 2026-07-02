package com.railconnect.booking.dtorequestresponse;

import com.railconnect.common.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PassengerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Gender gender
) {
}