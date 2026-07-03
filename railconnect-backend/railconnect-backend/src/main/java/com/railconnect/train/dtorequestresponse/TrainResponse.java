package com.railconnect.train.dtorequestresponse;

import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainResponse {
    private Long id;
    private String number;
    private String name;
    private TrainType type;
    private TrainStatus status;
    private Long routeId;
    private List<CoachResponse> coaches; // Linked dynamically from your coach mapping
}