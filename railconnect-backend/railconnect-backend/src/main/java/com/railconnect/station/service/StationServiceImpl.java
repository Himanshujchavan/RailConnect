package com.railconnect.station.service;

import com.railconnect.station.dtorequestresponse.StationRequest;
import com.railconnect.station.dtorequestresponse.StationResponse;
import com.railconnect.station.entity.Station;
import com.railconnect.station.mapper.StationMapper;
import com.railconnect.station.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl implements StationService {

    private final StationRepository repository;
    private final StationMapper mapper;

    public StationServiceImpl(StationRepository repository, StationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public StationResponse createStation(StationRequest request) {
        if (repository.existsByStationCode(request.getStationCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Station code already exists");
        }
        if (repository.existsByStationName(request.getStationName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Station name already exists");
        }
        
        Station station = mapper.toEntity(request);
        return mapper.toResponse(repository.save(station));
    }

    @Override
    public List<StationResponse> getAllStations() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StationResponse getStationById(Long id) {
        Station station = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found"));
        return mapper.toResponse(station);
    }

    @Override
    public List<StationResponse> searchStations(String keyword) {
        return repository.searchStations(keyword).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StationResponse updateStation(Long id, StationRequest request) {
        Station existingStation = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found"));

        // Allow updating if it's the same station entity holding that code/name
        if (!existingStation.getStationCode().equalsIgnoreCase(request.getStationCode()) &&
                repository.existsByStationCode(request.getStationCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Station code already exists");
        }

        existingStation.setStationCode(request.getStationCode().toUpperCase());
        existingStation.setStationName(request.getStationName());
        existingStation.setCity(request.getCity());
        existingStation.setState(request.getState());

        return mapper.toResponse(repository.save(existingStation));
    }

    @Override
    public void deleteStation(Long id) {
        Station existingStation = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found"));
        
        // TODO: Business Rule validation (Do not delete if used by a route)
        // Once Route module is created, inject RouteRepository and check here.

        repository.delete(existingStation);
    }
}