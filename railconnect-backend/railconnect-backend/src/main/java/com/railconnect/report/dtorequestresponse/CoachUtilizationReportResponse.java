package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;
import java.util.List;

public record CoachUtilizationReportResponse(
        LocalDate from,
        LocalDate to,
        List<CoachUtilizationResponse> coachTypes
) {
}
