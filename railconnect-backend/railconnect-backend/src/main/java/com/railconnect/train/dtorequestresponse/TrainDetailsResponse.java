package com.railconnect.train.dtorequestresponse;

import com.railconnect.common.enums.TrainType;

import java.util.List;

public record TrainDetailsResponse(
        Long trainId,
        String number,
        String name,
        TrainType type,
        List<Long> coachIds,
        List<Long> scheduleIds
) {
}