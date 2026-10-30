package com.railconnect.search.service;

import com.railconnect.admin.service.FareManagementService;
import com.railconnect.common.enums.CoachType;
import com.railconnect.entity.Coach;
import com.railconnect.entity.Route;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.search.dtorequestresponse.CoachAvailabilityResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StationService stationService;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private CoachRepository coachRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private TrainMapper trainMapper;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private FareManagementService fareManagementService;

    private SearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchServiceImpl(
                stationRepository,
                stationService,
                routeRepository,
                scheduleRepository,
                coachRepository,
                seatRepository,
                trainMapper,
                routeMapper,
                fareManagementService
        );
    }

    @Test
    void searchTrains_findsTrainsForRouteAndDay() {
        Station sourceStation = new Station();
        sourceStation.setId(1L);
        sourceStation.setStationCode("NDLS");

        Station destStation = new Station();
        destStation.setId(2L);
        destStation.setStationCode("MMCT");

        when(stationRepository.findByStationCode("NDLS")).thenReturn(Optional.of(sourceStation));
        when(stationRepository.findByStationCode("MMCT")).thenReturn(Optional.of(destStation));

        Route route = new Route();
        route.setId(5L);
        when(routeRepository.findRoutesBetweenStations(1L, 2L)).thenReturn(List.of(route));

        Train train = new Train();
        train.setId(10L);
        train.setNumber("12952");

        Schedule schedule = new Schedule();
        schedule.setId(20L);
        schedule.setTrain(train);
        schedule.setRoute(route);

        when(scheduleRepository.findByRouteAndOperatingDay(eq(5L), anyString())).thenReturn(List.of(schedule));
        when(trainMapper.toSearchResponse(train, route, schedule)).thenReturn(mock(TrainSearchResponse.class));

        LocalDate journeyDate = LocalDate.now();
        List<TrainSearchResponse> results = searchService.searchTrains("NDLS", "MMCT", journeyDate);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void searchAvailability_returnsAvailableSeatsPerCoach() {
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        Train train = new Train();
        train.setId(10L);
        schedule.setTrain(train);

        Coach coach1 = new Coach();
        coach1.setId(100L);
        coach1.setCoachNumber("B1");
        coach1.setCoachType("THIRD_AC");
        coach1.setSeatCount(72);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(coachRepository.findByTrainId(10L)).thenReturn(List.of(coach1));
        when(seatRepository.findAvailableSeatsByCoach(eq(100L), eq(1L), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<CoachAvailabilityResponse> availability = searchService.searchAvailability(1L, LocalDate.now());

        assertEquals(1, availability.size());
        assertEquals("B1", availability.get(0).coachNumber());
        assertEquals(72, availability.get(0).totalSeats());
        assertEquals(0, availability.get(0).availableSeats());
    }
}
