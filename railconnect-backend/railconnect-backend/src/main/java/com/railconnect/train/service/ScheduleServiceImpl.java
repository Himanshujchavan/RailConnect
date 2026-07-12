package com.railconnect.train.service;

import com.railconnect.entity.Route;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.train.repository.RouteRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.train.repository.TrainRepository;
import com.railconnect.train.mapper.ScheduleMapper;

import com.railconnect.journey.dtorequestresponse.ScheduleRequest;
import com.railconnect.journey.dtorequestresponse.ScheduleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, 
                               TrainRepository trainRepository, 
                               RouteRepository routeRepository, 
                               ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found"));

        Schedule schedule = scheduleMapper.toEntity(request);
        schedule.setTrain(train);
        schedule.setRoute(route);

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(savedSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule entry not found"));
        return scheduleMapper.toResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getSchedulesByTrainId(Long trainId) {
        return scheduleRepository.findByTrainId(trainId).stream()
                .map(scheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule entry not found"));

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route not found"));

        schedule.setTrain(train);
        schedule.setRoute(route);
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());
        schedule.setOperatingDays(request.getOperatingDays());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toResponse(savedSchedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule entry not found");
        }
        scheduleRepository.deleteById(id);
    }
}