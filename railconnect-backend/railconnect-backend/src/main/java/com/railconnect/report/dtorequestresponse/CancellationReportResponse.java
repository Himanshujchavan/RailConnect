package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;

public record CancellationReportResponse(
        LocalDate from,
        LocalDate to,
        long totalBookings,
        long cancelledBookings,
        double cancellationPercent
) {
}
