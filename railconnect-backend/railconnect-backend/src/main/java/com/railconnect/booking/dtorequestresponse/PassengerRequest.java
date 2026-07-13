package com.railconnect.booking.dtorequestresponse;

import com.railconnect.common.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PassengerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Gender gender,

        // Optional. Drives senior-citizen lower-berth priority and fare concessions
        // (Dynamic Pricing / Smart Seat Allocation). Null is treated as "unknown adult".
        @Min(0) Integer age
) {
}