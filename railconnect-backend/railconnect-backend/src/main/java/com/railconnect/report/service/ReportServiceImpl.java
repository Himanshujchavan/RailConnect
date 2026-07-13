package com.railconnect.report.service;

import com.railconnect.common.enums.BookingStatus;
import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Payment;
import com.railconnect.entity.Route;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.report.dtorequestresponse.CancellationReportResponse;
import com.railconnect.report.dtorequestresponse.CoachUtilizationReportResponse;
import com.railconnect.report.dtorequestresponse.CoachUtilizationResponse;
import com.railconnect.report.dtorequestresponse.HighestRevenueRoutesReportResponse;
import com.railconnect.report.dtorequestresponse.MostBookedTrainResponse;
import com.railconnect.report.dtorequestresponse.MostBookedTrainsReportResponse;
import com.railconnect.report.dtorequestresponse.OccupancyHeatmapCell;
import com.railconnect.report.dtorequestresponse.OccupancyHeatmapResponse;
import com.railconnect.report.dtorequestresponse.OccupancyReportResponse;
import com.railconnect.report.dtorequestresponse.PassengerReportResponse;
import com.railconnect.report.dtorequestresponse.PopularRouteResponse;
import com.railconnect.report.dtorequestresponse.PopularRoutesReportResponse;
import com.railconnect.report.dtorequestresponse.RevenueReportResponse;
import com.railconnect.report.dtorequestresponse.RouteRevenueResponse;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.train.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final SeatRepository seatRepository;
    private final ScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;

    private static final String BOOKED = "BOOKED";

    public ReportServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              SeatAllocationRepository seatAllocationRepository,
                              SeatRepository seatRepository,
                              ScheduleRepository scheduleRepository,
                              CoachRepository coachRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.seatRepository = seatRepository;
        this.scheduleRepository = scheduleRepository;
        this.coachRepository = coachRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueReportResponse getRevenueReport(LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<Payment> settledPayments = paymentRepository.findByStatusInAndPaidAtBetween(
                List.of(PaymentStatus.PAID, PaymentStatus.PARTIALLY_REFUNDED, PaymentStatus.REFUNDED),
                fromDt, toDt);

        double gross = 0.0;
        double refunded = 0.0;
        for (Payment payment : settledPayments) {
            gross += payment.amount != null ? payment.amount : 0.0;
            refunded += payment.refundedAmount != null ? payment.refundedAmount : 0.0;
        }

        return new RevenueReportResponse(from, to, round2(gross), round2(refunded), round2(gross - refunded),
                settledPayments.size());
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerReportResponse getPassengerReport(LocalDate from, LocalDate to) {
        List<Booking> bookings = bookingRepository.findByJourneyDateBetween(from, to);

        long totalPassengers = 0;
        Map<String, Long> byStatus = new HashMap<>();

        for (Booking booking : bookings) {
            int passengerCount = booking.bookingPassengers == null ? 0 : booking.bookingPassengers.size();
            totalPassengers += passengerCount;
            String statusKey = booking.status != null ? booking.status.name() : "UNKNOWN";
            byStatus.merge(statusKey, (long) passengerCount, Long::sum);
        }

        return new PassengerReportResponse(from, to, bookings.size(), totalPassengers, byStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public OccupancyReportResponse getOccupancyReport(LocalDate from, LocalDate to) {
        long bookedSeatDays = seatAllocationRepository.countByJourneyDateBetweenAndStatus(from, to, BOOKED);
        long totalSeats = seatRepository.count();
        long days = daysInRange(from, to);
        long totalSeatCapacity = totalSeats * days;

        double occupancyPercent = totalSeatCapacity > 0 ? round2(bookedSeatDays * 100.0 / totalSeatCapacity) : 0.0;

        return new OccupancyReportResponse(from, to, bookedSeatDays, totalSeatCapacity, occupancyPercent);
    }

    @Override
    @Transactional(readOnly = true)
    public CancellationReportResponse getCancellationReport(LocalDate from, LocalDate to) {
        long total = bookingRepository.countByJourneyDateBetween(from, to);
        long cancelled = bookingRepository.countByJourneyDateBetweenAndStatus(from, to, BookingStatus.CANCELLED);
        double percent = total > 0 ? round2(cancelled * 100.0 / total) : 0.0;

        return new CancellationReportResponse(from, to, total, cancelled, percent);
    }

    @Override
    @Transactional(readOnly = true)
    public PopularRoutesReportResponse getPopularRoutes(LocalDate from, LocalDate to, int limit) {
        List<Booking> bookings = bookingRepository.findByJourneyDateBetween(from, to);

        Map<Long, Long> countByScheduleId = new HashMap<>();
        for (Booking booking : bookings) {
            if (booking.scheduleId != null) {
                countByScheduleId.merge(booking.scheduleId, 1L, Long::sum);
            }
        }

        Set<Long> scheduleIds = new LinkedHashSet<>(countByScheduleId.keySet());
        List<Schedule> schedules = scheduleRepository.findAllById(scheduleIds);

        Map<Long, Long> countByRouteId = new HashMap<>();
        Map<Long, Route> routeById = new HashMap<>();
        for (Schedule schedule : schedules) {
            Route route = schedule.getRoute();
            if (route == null) {
                continue;
            }
            Long count = countByScheduleId.get(schedule.getId());
            if (count == null) {
                continue;
            }
            routeById.put(route.getId(), route);
            countByRouteId.merge(route.getId(), count, Long::sum);
        }

        List<PopularRouteResponse> topRoutes = countByRouteId.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(Math.max(limit, 1))
                .map(entry -> {
                    Route route = routeById.get(entry.getKey());
                    return new PopularRouteResponse(
                            route.getId(),
                            route.getRouteName(),
                            route.getSourceStation() != null ? route.getSourceStation().getStationCode() : null,
                            route.getDestinationStation() != null ? route.getDestinationStation().getStationCode() : null,
                            entry.getValue());
                })
                .toList();

        return new PopularRoutesReportResponse(from, to, topRoutes);
    }

    @Override
    @Transactional(readOnly = true)
    public CoachUtilizationReportResponse getCoachUtilization(LocalDate from, LocalDate to) {
        long days = daysInRange(from, to);

        Map<String, Long> bookedByType = new HashMap<>();
        for (Object[] row : seatAllocationRepository.countBookedByCoachType(from, to, BOOKED)) {
            bookedByType.put((String) row[0], (Long) row[1]);
        }

        Map<String, Long> totalByType = new HashMap<>();
        for (Object[] row : seatRepository.countSeatsByCoachType()) {
            totalByType.put((String) row[0], (Long) row[1]);
        }

        Set<String> coachTypes = new LinkedHashSet<>();
        coachTypes.addAll(totalByType.keySet());
        coachTypes.addAll(bookedByType.keySet());

        List<CoachUtilizationResponse> breakdown = coachTypes.stream()
                .map(coachType -> {
                    long booked = bookedByType.getOrDefault(coachType, 0L);
                    long totalSeats = totalByType.getOrDefault(coachType, 0L);
                    long capacity = totalSeats * days;
                    double percent = capacity > 0 ? round2(booked * 100.0 / capacity) : 0.0;
                    return new CoachUtilizationResponse(coachType, booked, capacity, percent);
                })
                .sorted(Comparator.comparing(CoachUtilizationResponse::utilizationPercent).reversed())
                .toList();

        return new CoachUtilizationReportResponse(from, to, breakdown);
    }

    @Override
    @Transactional(readOnly = true)
    public MostBookedTrainsReportResponse getMostBookedTrains(LocalDate from, LocalDate to, int limit) {
        Map<Long, Long> countByTrainId = new HashMap<>();
        Map<Long, Train> trainById = new HashMap<>();

        for (Map.Entry<Schedule, Long> entry : bookingCountByResolvedSchedule(from, to).entrySet()) {
            Train train = entry.getKey().getTrain();
            if (train == null) {
                continue;
            }
            trainById.put(train.getId(), train);
            countByTrainId.merge(train.getId(), entry.getValue(), Long::sum);
        }

        List<MostBookedTrainResponse> trains = countByTrainId.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(Math.max(limit, 1))
                .map(entry -> {
                    Train train = trainById.get(entry.getKey());
                    return new MostBookedTrainResponse(train.getId(), train.getNumber(), train.getName(), entry.getValue());
                })
                .toList();

        return new MostBookedTrainsReportResponse(from, to, trains);
    }

    @Override
    @Transactional(readOnly = true)
    public HighestRevenueRoutesReportResponse getHighestRevenueRoutes(LocalDate from, LocalDate to, int limit) {
        LocalDateTime fromDt = from.atStartOfDay();
        LocalDateTime toDt = to.atTime(LocalTime.MAX);

        List<Payment> settledPayments = paymentRepository.findByStatusInAndPaidAtBetween(
                List.of(PaymentStatus.PAID, PaymentStatus.PARTIALLY_REFUNDED, PaymentStatus.REFUNDED),
                fromDt, toDt);

        Map<Long, Double> revenueByScheduleId = new HashMap<>();
        for (Payment payment : settledPayments) {
            if (payment.booking == null || payment.booking.scheduleId == null) {
                continue;
            }
            double net = (payment.amount != null ? payment.amount : 0.0)
                    - (payment.refundedAmount != null ? payment.refundedAmount : 0.0);
            revenueByScheduleId.merge(payment.booking.scheduleId, net, Double::sum);
        }

        Set<Long> scheduleIds = new LinkedHashSet<>(revenueByScheduleId.keySet());
        List<Schedule> schedules = scheduleRepository.findAllById(scheduleIds);

        Map<Long, Double> revenueByRouteId = new HashMap<>();
        Map<Long, Route> routeById = new HashMap<>();
        for (Schedule schedule : schedules) {
            Route route = schedule.getRoute();
            Double revenue = revenueByScheduleId.get(schedule.getId());
            if (route == null || revenue == null) {
                continue;
            }
            routeById.put(route.getId(), route);
            revenueByRouteId.merge(route.getId(), revenue, Double::sum);
        }

        List<RouteRevenueResponse> topRoutes = revenueByRouteId.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(Math.max(limit, 1))
                .map(entry -> {
                    Route route = routeById.get(entry.getKey());
                    return new RouteRevenueResponse(
                            route.getId(),
                            route.getRouteName(),
                            route.getSourceStation() != null ? route.getSourceStation().getStationCode() : null,
                            route.getDestinationStation() != null ? route.getDestinationStation().getStationCode() : null,
                            round2(entry.getValue()));
                })
                .toList();

        return new HighestRevenueRoutesReportResponse(from, to, topRoutes);
    }

    @Override
    @Transactional(readOnly = true)
    public OccupancyHeatmapResponse getOccupancyHeatmap(LocalDate from, LocalDate to, int trainLimit) {
        MostBookedTrainsReportResponse topTrains = getMostBookedTrains(from, to, trainLimit);

        List<OccupancyHeatmapCell> cells = new java.util.ArrayList<>();

        for (MostBookedTrainResponse train : topTrains.trains()) {
            List<Schedule> trainSchedules = scheduleRepository.findByTrainId(train.trainId());
            List<Long> scheduleIds = trainSchedules.stream().map(Schedule::getId).toList();

            int totalCapacity = coachRepository.findByTrainId(train.trainId()).stream()
                    .filter(coach -> Boolean.TRUE.equals(coach.getIsActive()))
                    .mapToInt(coach -> coach.getSeatCount() != null ? coach.getSeatCount() : 0)
                    .sum();

            for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
                long booked = 0;
                for (Long scheduleId : scheduleIds) {
                    booked += seatAllocationRepository.findOccupiedSeats(scheduleId, date).size();
                }
                double occupancyPercent = totalCapacity > 0 ? round2(booked * 100.0 / totalCapacity) : 0.0;
                cells.add(new OccupancyHeatmapCell(date, train.trainId(), train.trainNumber(), occupancyPercent));
            }
        }

        return new OccupancyHeatmapResponse(from, to, cells);
    }

    /**
     * Shared by Most Booked Train and (indirectly, via train) the heatmap: how many bookings
     * fall on each resolved {@link Schedule} in the range.
     */
    private Map<Schedule, Long> bookingCountByResolvedSchedule(LocalDate from, LocalDate to) {
        List<Booking> bookings = bookingRepository.findByJourneyDateBetween(from, to);

        Map<Long, Long> countByScheduleId = new HashMap<>();
        for (Booking booking : bookings) {
            if (booking.scheduleId != null) {
                countByScheduleId.merge(booking.scheduleId, 1L, Long::sum);
            }
        }

        Set<Long> scheduleIds = new LinkedHashSet<>(countByScheduleId.keySet());
        List<Schedule> schedules = scheduleRepository.findAllById(scheduleIds);

        Map<Schedule, Long> result = new HashMap<>();
        for (Schedule schedule : schedules) {
            Long count = countByScheduleId.get(schedule.getId());
            if (count != null) {
                result.put(schedule, count);
            }
        }
        return result;
    }

    private long daysInRange(LocalDate from, LocalDate to) {
        return Math.max(ChronoUnit.DAYS.between(from, to) + 1, 1);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
