package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;

public record RevenueReportResponse(
        LocalDate from,
        LocalDate to,
        double grossRevenue,
        double totalRefunded,
        double netRevenue,
        long settledPaymentCount
) {
}
