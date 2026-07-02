package com.railconnect.train.dtorequestresponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TrainSearchRequest(
        @NotBlank String sourceStationCode,
        @NotBlank String destinationStationCode,
        @NotNull LocalDate journeyDate
) {
}