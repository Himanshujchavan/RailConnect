package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;
import java.util.List;

public record MostBookedTrainsReportResponse(
        LocalDate from,
        LocalDate to,
        List<MostBookedTrainResponse> trains
) {
}
