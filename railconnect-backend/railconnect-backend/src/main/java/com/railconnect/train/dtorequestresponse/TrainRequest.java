package com.railconnect.train.dtorequestresponse;

import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TrainRequest {

    @NotBlank(message = "Train number is required")
    @Size(min = 5, max = 10, message = "Train number must be between 5 and 10 characters")
    private String number;

    @NotBlank(message = "Train name is required")
    private String name;

    @NotNull(message = "Train type is required")
    private TrainType type;

    @NotNull(message = "Train status is required")
    private TrainStatus status;

    @NotNull(message = "Route ID is required")
    private Long routeId;
}