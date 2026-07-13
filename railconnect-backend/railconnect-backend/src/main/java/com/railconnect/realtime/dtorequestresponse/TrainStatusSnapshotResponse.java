package com.railconnect.realtime.dtorequestresponse;

import com.railconnect.common.enums.TrainStatus;

public record TrainStatusSnapshotResponse(
        Long trainId,
        String trainNumber,
        TrainStatus status
) {
}
