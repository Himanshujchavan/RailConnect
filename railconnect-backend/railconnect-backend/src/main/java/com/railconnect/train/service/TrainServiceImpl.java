package com.railconnect.train.service;

import com.railconnect.entity.Train;
import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;
import com.railconnect.train.dtorequestresponse.TrainRequest; // Matches your actual package spelling
import com.railconnect.train.dtorequestresponse.TrainResponse;
import com.railconnect.train.mapper.TrainMapper;
import com.railconnect.train.repository.TrainRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;

    public TrainServiceImpl(TrainRepository trainRepository, TrainMapper trainMapper) {
        this.trainRepository = trainRepository;
        this.trainMapper = trainMapper;
    }

    @Override
    @Transactional
    public TrainResponse createTrain(TrainRequest request) {
        if (trainRepository.existsByNumber(request.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Train number already exists");
        }
        
        Train train = trainMapper.toEntity(request);
        Train savedTrain = trainRepository.save(train);
        return trainMapper.toResponse(savedTrain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainResponse> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(trainMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainResponse getTrainById(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));
        return trainMapper.toResponse(train);
    }

    @Override
    @Transactional
    public TrainResponse updateTrain(Long id, TrainRequest request) {
        Train existingTrain = trainRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));

        // If the number is changing, verify the new one is not taken
        if (!existingTrain.getNumber().equals(request.getNumber()) && 
            trainRepository.existsByNumber(request.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Train number already exists");
        }

        trainMapper.updateEntityFromDto(request, existingTrain);
        Train updatedTrain = trainRepository.save(existingTrain);
        return trainMapper.toResponse(updatedTrain);
    }

    @Override
    @Transactional
    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found");
        }
        trainRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TrainResponse updateTrainStatus(Long id, TrainStatus status) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));
        train.setStatus(status);
        return trainMapper.toResponse(trainRepository.save(train));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainResponse> getTrainsByType(TrainType type) {
        return trainRepository.findByType(type).stream()
                .map(trainMapper::toResponse)
                .collect(Collectors.toList());
    }
}