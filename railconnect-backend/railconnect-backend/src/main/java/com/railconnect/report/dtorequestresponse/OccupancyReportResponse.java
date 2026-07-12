package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;

/**
 * {@code totalSeatCapacity} is a simplification: total distinct seats in the system multiplied
 * by the number of days in the range, i.e. it assumes every seat's train runs every day of the
 * range. Good enough for a headline occupancy % without needing per-schedule calendars; treat
 * it as an estimate, not an exact seat-day count.
 */
public record OccupancyReportResponse(
        LocalDate from,
        LocalDate to,
        long bookedSeatDays,
        long totalSeatCapacity,
        double occupancyPercent
) {
}
