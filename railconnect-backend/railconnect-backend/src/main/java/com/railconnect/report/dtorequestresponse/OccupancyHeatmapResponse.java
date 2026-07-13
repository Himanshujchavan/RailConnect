package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;
import java.util.List;

/**
 * A date x train grid of occupancy %, meant to be rendered as a heatmap (date on one axis,
 * train on the other, color intensity = occupancyPercent). Limited to the busiest
 * {@code trainLimit} trains in the range so the grid stays a manageable size instead of
 * including every train in the fleet.
 */
public record OccupancyHeatmapResponse(
        LocalDate from,
        LocalDate to,
        List<OccupancyHeatmapCell> cells
) {
}
