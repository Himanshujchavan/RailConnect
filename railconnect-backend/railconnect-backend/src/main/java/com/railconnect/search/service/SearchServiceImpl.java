package com.railconnect.search.service;

import com.railconnect.admin.service.FareManagementService;
import com.railconnect.common.enums.CoachType;
import com.railconnect.common.exception.InvalidStationException;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.common.util.FareCalculator;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Route;
import com.railconnect.entity.RouteStation;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.journey.dtorequestresponse.RouteResponse;
import com.railconnect.search.dtorequestresponse.CoachAvailabilityResponse;
import com.railconnect.search.dtorequestresponse.FareSearchResponse;
import com.railconnect.station.dtorequestresponse.StationResponse;
import com.railconnect.station.entity.Station;
import com.railconnect.station.repository.StationRepository;
import com.railconnect.station.service.StationService;
import com.railconnect.train.dtorequestresponse.TrainSearchResponse;
import com.railconnect.train.mapper.RouteMapper;
import com.railconnect.train.mapper.TrainMapper;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.RouteRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.train.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final StationRepository stationRepository;
    private final StationService stationService;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final TrainMapper trainMapper;
    private final RouteMapper routeMapper;
    private final FareManagementService fareManagementService;

    public SearchServiceImpl(StationRepository stationRepository,
                              StationService stationService,
                              RouteRepository routeRepository,
                              ScheduleRepository scheduleRepository,
                              CoachRepository coachRepository,
                              SeatRepository seatRepository,
                              TrainMapper trainMapper,
                              RouteMapper routeMapper,
                              FareManagementService fareManagementService) {
        this.stationRepository = stationRepository;
        this.stationService = stationService;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
        this.trainMapper = trainMapper;
        this.routeMapper = routeMapper;
        this.fareManagementService = fareManagementService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainSearchResponse> searchTrains(String sourceStationCode, String destinationStationCode,
                                                   LocalDate journeyDate) {
        Station source = findStation(sourceStationCode);
        Station destination = findStation(destinationStationCode);

        String dayOfWeek = journeyDate.getDayOfWeek().name();
        List<Route> routes = routeRepository.findRoutesBetweenStations(source.getId(), destination.getId());

        return routes.stream()
                .flatMap(route -> scheduleRepository.findByRouteAndOperatingDay(route.getId(), dayOfWeek).stream()
                        .map(schedule -> trainMapper.toSearchResponse(schedule.getTrain(), route, schedule)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> searchRoutes(String sourceStationCode, String destinationStationCode) {
        Station source = findStation(sourceStationCode);
        Station destination = findStation(destinationStationCode);

        return routeRepository.findRoutesBetweenStations(source.getId(), destination.getId()).stream()
                .map(routeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StationResponse> searchStations(String keyword) {
        return stationService.searchStations(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachAvailabilityResponse> searchAvailability(Long scheduleId, LocalDate journeyDate) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        Train train = schedule.getTrain();
        List<Coach> coaches = coachRepository.findByTrainId(train.getId());

        return coaches.stream()
                .map(coach -> new CoachAvailabilityResponse(
                        coach.getId(),
                        coach.getCoachNumber(),
                        coach.getCoachType(),
                        coach.getSeatCount(),
                        seatRepository.findAvailableSeatsByCoach(coach.getId(), scheduleId, journeyDate).size()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FareSearchResponse searchFare(Long scheduleId, String sourceStationCode, String destinationStationCode,
                                         CoachType coachType, int passengerAge) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        Route route = schedule.getRoute();
        RouteStation sourceStop = findRouteStation(route, sourceStationCode);
        RouteStation destinationStop = findRouteStation(route, destinationStationCode);

        double distanceKm = Math.abs(destinationStop.getDistanceFromSource() - sourceStop.getDistanceFromSource());
        Double rateOverride = fareManagementService.getEffectiveRatePerKm(coachType);
        double fare = FareCalculator.calculateFare(distanceKm, coachType, passengerAge, rateOverride);

        return new FareSearchResponse(scheduleId, sourceStationCode.toUpperCase(), destinationStationCode.toUpperCase(),
                distanceKm, coachType, passengerAge, fare);
    }

    private Station findStation(String stationCode) {
        return stationRepository.findByStationCode(stationCode.toUpperCase())
                .orElseThrow(() -> new InvalidStationException(stationCode));
    }

    private RouteStation findRouteStation(Route route, String stationCode) {
        return route.getRouteStations().stream()
                .filter(rs -> rs.getStation().getStationCode().equalsIgnoreCase(stationCode))
                .findFirst()
                .orElseThrow(() -> new InvalidStationException(stationCode));
    }
}
