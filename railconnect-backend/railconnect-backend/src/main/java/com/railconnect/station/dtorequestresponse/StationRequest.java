package com.railconnect.station.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StationRequest {
    @NotBlank(message = "Station code is required")
    // --- UPDATE THIS LINE TO ALLOW 1 TO 4 CHARACTERS ---
    @Size(min = 1, max = 4, message = "Station code must be between 1 and 4 characters")
    private String stationCode;

    @NotBlank(message = "Station name is required")
    private String stationName;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;
}