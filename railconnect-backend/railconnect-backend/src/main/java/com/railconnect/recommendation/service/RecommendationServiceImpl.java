package com.railconnect.recommendation.service;

import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.dashboard.mapper.DashboardMapper;
import com.railconnect.dashboard.repository.FavouriteRouteRepository;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Route;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.recommendation.dtorequestresponse.CoachRecommendationResponse;
import com.railconnect.recommendation.dtorequestresponse.FrequentRouteResponse;
import com.railconnect.recommendation.dtorequestresponse.SuggestedTrainResponse;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final UserRepository userRepository;
    private final FavouriteRouteRepository favouriteRouteRepository;
    private final DashboardMapper dashboardMapper;

    public RecommendationServiceImpl(BookingRepository bookingRepository,
                                      ScheduleRepository scheduleRepository,
                                      CoachRepository coachRepository,
                                      SeatAllocationRepository seatAllocationRepository,
                                      UserRepository userRepository,
                                      FavouriteRouteRepository favouriteRouteRepository,
                                      DashboardMapper dashboardMapper) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.coachRepository = coachRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.userRepository = userRepository;
        this.favouriteRouteRepository = favouriteRouteRepository;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FrequentRouteResponse> getFrequentRoutes(Long userId, int limit) {
        ensureUserExists(userId);
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (bookings.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> countByScheduleId = new HashMap<>();
        Map<Long, LocalDate> lastTravelledByScheduleId = new HashMap<>();
        for (Booking booking : bookings) {
            if (booking.scheduleId == null) {
                continue;
            }
            countByScheduleId.merge(booking.scheduleId, 1L, Long::sum);
            lastTravelledByScheduleId.merge(booking.scheduleId, booking.journeyDate,
                    (a, b) -> a.isAfter(b) ? a : b);
        }

        Set<Long> scheduleIds = new LinkedHashSet<>(countByScheduleId.keySet());
        List<Schedule> schedules = scheduleRepository.findAllById(scheduleIds);

        Map<Long, Long> countByRouteId = new HashMap<>();
        Map<Long, LocalDate> lastTravelledByRouteId = new HashMap<>();
        Map<Long, Route> routeById = new HashMap<>();

        for (Schedule schedule : schedules) {
            Route route = schedule.getRoute();
            if (route == null) {
                continue;
            }
            Long count = countByScheduleId.get(schedule.getId());
            LocalDate lastTravelled = lastTravelledByScheduleId.get(schedule.getId());
            if (count == null) {
                continue;
            }
            routeById.put(route.getId(), route);
            countByRouteId.merge(route.getId(), count, Long::sum);
            lastTravelledByRouteId.merge(route.getId(), lastTravelled, (a, b) -> a.isAfter(b) ? a : b);
        }

        return countByRouteId.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(Math.max(limit, 1))
                .map(entry -> {
                    Route route = routeById.get(entry.getKey());
                    return new FrequentRouteResponse(
                            route.getId(),
                            route.getRouteName(),
                            route.getSourceStation() != null ? route.getSourceStation().getStationCode() : null,
                            route.getDestinationStation() != null ? route.getDestinationStation().getStationCode() : null,
                            entry.getValue(),
                            lastTravelledByRouteId.get(entry.getKey()));
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuggestedTrainResponse> getSuggestedTrains(Long userId, int limit) {
        List<FrequentRouteResponse> frequentRoutes = getFrequentRoutes(userId, 1);
        if (frequentRoutes.isEmpty()) {
            return List.of();
        }

        Long topRouteId = frequentRoutes.get(0).routeId();
        return scheduleRepository.findByRouteId(topRouteId).stream()
                .limit(Math.max(limit, 1))
                .map(schedule -> new SuggestedTrainResponse(
                        schedule.getTrain().getId(),
                        schedule.getTrain().getNumber(),
                        schedule.getTrain().getName(),
                        schedule.getId(),
                        topRouteId,
                        schedule.getDepartureTime(),
                        schedule.getArrivalTime()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachRecommendationResponse> getCoachRecommendations(Long userId, Long scheduleId,
                                                                      LocalDate journeyDate, int partySize,
                                                                      int limit) {
        ensureUserExists(userId);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        String preferredCoachType = findPreferredCoachType(userId);

        List<Coach> candidateCoaches = coachRepository.findByTrainId(schedule.getTrain().getId()).stream()
                .filter(coach -> Boolean.TRUE.equals(coach.getIsActive()))
                .toList();

        Set<Long> occupiedSeatIds = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate).stream()
                .map(SeatAllocation::getSeat)
                .map(seat -> seat.getId())
                .collect(Collectors.toSet());

        return candidateCoaches.stream()
                .map(coach -> {
                    long occupiedInCoach = coach.getSeats().stream()
                            .filter(seat -> occupiedSeatIds.contains(seat.getId()))
                            .count();
                    int available = (int) (coach.getSeatCount() - occupiedInCoach);
                    boolean matchesPreference = preferredCoachType != null
                            && preferredCoachType.equalsIgnoreCase(coach.getCoachType());
                    String reason = matchesPreference
                            ? "Matches your usual " + coach.getCoachType() + " choice"
                            : "Good availability (" + Math.max(available, 0) + " seats free)";
                    return new Object[]{coach, available, matchesPreference, reason};
                })
                .filter(row -> (int) row[1] >= partySize)
                .sorted(Comparator
                        .comparing((Object[] row) -> (boolean) row[2]).reversed()
                        .thenComparing(row -> (int) row[1], Comparator.reverseOrder()))
                .limit(Math.max(limit, 1))
                .map(row -> {
                    Coach coach = (Coach) row[0];
                    int available = (int) row[1];
                    String reason = (String) row[3];
                    return new CoachRecommendationResponse(
                            coach.getId(), coach.getCoachNumber(), coach.getCoachType(), available, reason);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteRouteResponse> getSavedRoutes(Long userId) {
        ensureUserExists(userId);
        return favouriteRouteRepository.findByUserId(userId).stream()
                .map(dashboardMapper::toResponse)
                .toList();
    }

    /**
     * The coach type the user has booked most often in the past, or {@code null} if they have
     * no booking history (or their past coaches no longer resolve).
     */
    private String findPreferredCoachType(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (bookings.isEmpty()) {
            return null;
        }

        Set<Long> coachIds = bookings.stream()
                .map(b -> b.coachId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        List<Coach> coaches = coachRepository.findAllById(coachIds);
        Map<Long, String> coachTypeById = coaches.stream()
                .collect(Collectors.toMap(Coach::getId, Coach::getCoachType));

        Map<String, Long> countByType = new HashMap<>();
        for (Booking booking : bookings) {
            String coachType = coachTypeById.get(booking.coachId);
            if (coachType != null) {
                countByType.merge(coachType, 1L, Long::sum);
            }
        }

        return countByType.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }
}
