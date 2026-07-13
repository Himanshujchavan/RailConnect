package com.railconnect.realtime.dtorequestresponse;

import java.time.LocalDate;
import java.util.List;

public record SeatAvailabilitySnapshotResponse(
        Long scheduleId,
        LocalDate journeyDate,
        List<CoachAvailabilitySnapshot> coaches
) {
}
