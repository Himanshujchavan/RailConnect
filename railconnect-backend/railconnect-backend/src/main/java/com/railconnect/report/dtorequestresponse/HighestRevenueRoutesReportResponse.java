package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;
import java.util.List;

public record HighestRevenueRoutesReportResponse(
        LocalDate from,
        LocalDate to,
        List<RouteRevenueResponse> routes
) {
}
