package com.railconnect.report.service;

import com.railconnect.report.dtorequestresponse.CancellationReportResponse;
import com.railconnect.report.dtorequestresponse.CoachUtilizationReportResponse;
import com.railconnect.report.dtorequestresponse.HighestRevenueRoutesReportResponse;
import com.railconnect.report.dtorequestresponse.MostBookedTrainsReportResponse;
import com.railconnect.report.dtorequestresponse.OccupancyHeatmapResponse;
import com.railconnect.report.dtorequestresponse.OccupancyReportResponse;
import com.railconnect.report.dtorequestresponse.PassengerReportResponse;
import com.railconnect.report.dtorequestresponse.PopularRoutesReportResponse;
import com.railconnect.report.dtorequestresponse.RevenueReportResponse;

import java.time.LocalDate;

/**
 * Phase 9 — Reporting.
 * <p>
 * Read-only analytics over existing data (Payment, Booking, SeatAllocation, Route) - this
 * module owns no entities of its own, it only aggregates. Every report is scoped to a
 * {@code [from, to]} journey-date (or paid-at, for revenue) window, inclusive on both ends.
 */
public interface ReportService {

    RevenueReportResponse getRevenueReport(LocalDate from, LocalDate to);

    PassengerReportResponse getPassengerReport(LocalDate from, LocalDate to);

    OccupancyReportResponse getOccupancyReport(LocalDate from, LocalDate to);

    CancellationReportResponse getCancellationReport(LocalDate from, LocalDate to);

    PopularRoutesReportResponse getPopularRoutes(LocalDate from, LocalDate to, int limit);

    CoachUtilizationReportResponse getCoachUtilization(LocalDate from, LocalDate to);

    MostBookedTrainsReportResponse getMostBookedTrains(LocalDate from, LocalDate to, int limit);

    HighestRevenueRoutesReportResponse getHighestRevenueRoutes(LocalDate from, LocalDate to, int limit);

    OccupancyHeatmapResponse getOccupancyHeatmap(LocalDate from, LocalDate to, int trainLimit);
}
