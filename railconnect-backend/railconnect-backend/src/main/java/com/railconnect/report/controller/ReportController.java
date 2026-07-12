package com.railconnect.report.controller;

import com.railconnect.report.dtorequestresponse.CancellationReportResponse;
import com.railconnect.report.dtorequestresponse.CoachUtilizationReportResponse;
import com.railconnect.report.dtorequestresponse.OccupancyReportResponse;
import com.railconnect.report.dtorequestresponse.PassengerReportResponse;
import com.railconnect.report.dtorequestresponse.PopularRoutesReportResponse;
import com.railconnect.report.dtorequestresponse.RevenueReportResponse;
import com.railconnect.report.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Phase 9 — Reporting.
 * <p>
 * Every report takes an inclusive {@code [from, to]} journey-date window (paid-at for revenue).
 * If omitted, defaults to the trailing 30 days ending today - a sensible headline window for a
 * dashboard landing on this without picking dates first.
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportResponse> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(reportService.getRevenueReport(range[0], range[1]));
    }

    @GetMapping("/passengers")
    public ResponseEntity<PassengerReportResponse> getPassengerReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(reportService.getPassengerReport(range[0], range[1]));
    }

    @GetMapping("/occupancy")
    public ResponseEntity<OccupancyReportResponse> getOccupancyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(reportService.getOccupancyReport(range[0], range[1]));
    }

    @GetMapping("/cancellations")
    public ResponseEntity<CancellationReportResponse> getCancellationReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(reportService.getCancellationReport(range[0], range[1]));
    }

    @GetMapping("/popular-routes")
    public ResponseEntity<PopularRoutesReportResponse> getPopularRoutes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "5") int limit) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(reportService.getPopularRoutes(range[0], range[1], limit));
    }

    @GetMapping("/coach-utilization")
    public ResponseEntity<CoachUtilizationReportResponse> getCoachUtilization(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate[] range = resolveRange(from, to);
        return ResponseEntity.ok(reportService.getCoachUtilization(range[0], range[1]));
    }

    private LocalDate[] resolveRange(LocalDate from, LocalDate to) {
        LocalDate resolvedTo = to != null ? to : LocalDate.now();
        LocalDate resolvedFrom = from != null ? from : resolvedTo.minusDays(30);
        return new LocalDate[]{resolvedFrom, resolvedTo};
    }
}
