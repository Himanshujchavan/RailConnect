package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;
import java.util.List;

public record PopularRoutesReportResponse(
        LocalDate from,
        LocalDate to,
        List<PopularRouteResponse> routes
) {
}
