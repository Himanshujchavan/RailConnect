package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;

public record OccupancyHeatmapCell(
        LocalDate date,
        Long trainId,
        String trainNumber,
        double occupancyPercent
) {
}
