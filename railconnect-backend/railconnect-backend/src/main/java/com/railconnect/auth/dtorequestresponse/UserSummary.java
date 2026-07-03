package com.railconnect.auth.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserSummary(
    @NotNull(message = "User ID cannot be null")
    Long id,

    @NotBlank(message = "Name cannot be blank")
    String name,

    @NotBlank(message = "Role cannot be blank")
    String role
) {}
