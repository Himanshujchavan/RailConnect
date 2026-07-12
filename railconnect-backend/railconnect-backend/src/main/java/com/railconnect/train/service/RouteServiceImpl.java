package com.railconnect.train.service;

import com.railconnect.entity.Route;
import com.railconnect.entity.RouteStation;
import com.railconnect.station.entity.Station;
import com.railconnect.train.repository.RouteRepository;
import com.railconnect.station.repository.StationRepository; // Assuming this package name
import com.railconnect.train.mapper.RouteMapper;

import com.railconnect.journey.dtorequestresponse.RouteRequest;
import com.railconnect.journey.dtorequestresponse.RouteStationRequest;
import com.railconnect.journey.dtorequestresponse.RouteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final RouteMapper routeMapper;

    public RouteServiceImpl(RouteRepository routeRepository, StationRepository stationRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
        this.routeMapper = routeMapper;
    }

    @Override
    @Transactional
    public RouteResponse createRoute(RouteRequest request) {
        if (routeRepository.existsByRouteName(request.getRouteName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Route name already exists");
        }

        Station source = stationRepository.findById(request.getSourceStationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source station not found"));

        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination station not found"));

        Route route = new Route();
        route.setRouteName(request.getRouteName());
        route.setSourceStation(source);
        route.setDestinationStation(destination);

        List<RouteStation> detailedStops = new ArrayList<>();
        int lastDistance = -1;

        for (RouteStationRequest stopReq : request.getRouteStations()) {
            Station stopStation = stationRepository.findById(stopReq.getStationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station inside route sequence not found"));

            if (stopReq.getDistanceFromSource() <= lastDistance) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Distances must increase progressively along the route tracking path");
            }
            lastDistance = stopReq.getDistanceFromSource();

            RouteStation routeStation = new RouteStation();
            routeStation.setRoute(route);
            routeStation.setStation(stopStation);
            routeStation.setStopOrder(stopReq.getStopOrder());
            routeStation.setDistanceFromSource(stopReq.getDistanceFromSource());
            detailedStops.add(routeStation);
        }

        route.setRouteStations(detailedStops);
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponse(savedRoute);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(routeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route profile not found"));
        return routeMapper.toResponse(route);
    }

    @Override
    @Transactional
    public RouteResponse updateRoute(Long id, RouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route profile not found"));

        if (routeRepository.existsByRouteNameAndIdNot(request.getRouteName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Route name already exists");
        }

        Station source = stationRepository.findById(request.getSourceStationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source station not found"));

        Station destination = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination station not found"));

        route.setRouteName(request.getRouteName());
        route.setSourceStation(source);
        route.setDestinationStation(destination);

        List<RouteStation> detailedStops = new ArrayList<>();
        int lastDistance = -1;

        for (RouteStationRequest stopReq : request.getRouteStations()) {
            Station stopStation = stationRepository.findById(stopReq.getStationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station inside route sequence not found"));

            if (stopReq.getDistanceFromSource() <= lastDistance) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Distances must increase progressively along the route tracking path");
            }
            lastDistance = stopReq.getDistanceFromSource();

            RouteStation routeStation = new RouteStation();
            routeStation.setRoute(route);
            routeStation.setStation(stopStation);
            routeStation.setStopOrder(stopReq.getStopOrder());
            routeStation.setDistanceFromSource(stopReq.getDistanceFromSource());
            detailedStops.add(routeStation);
        }

        // orphanRemoval=true on Route.routeStations means clearing + re-adding deletes the old rows.
        route.getRouteStations().clear();
        route.getRouteStations().addAll(detailedStops);

        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponse(savedRoute);
    }

    @Override
    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found");
        }
        routeRepository.deleteById(id);
    }
}