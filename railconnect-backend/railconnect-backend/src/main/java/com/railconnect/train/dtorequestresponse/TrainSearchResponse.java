package com.railconnect.train.dtorequestresponse;

import com.railconnect.common.enums.TrainType;

public record TrainSearchResponse(
        Long trainId,
        String number,
        String name,
        TrainType type,
        Long routeId,
        Long scheduleId
) {
}