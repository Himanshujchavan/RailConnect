package com.railconnect.report.dtorequestresponse;

import java.time.LocalDate;
import java.util.Map;

public record PassengerReportResponse(
        LocalDate from,
        LocalDate to,
        long totalBookings,
        long totalPassengers,
        Map<String, Long> passengersByBookingStatus
) {
}
