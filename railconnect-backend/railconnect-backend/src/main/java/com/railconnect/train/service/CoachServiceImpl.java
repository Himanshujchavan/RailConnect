package com.railconnect.train.service;

import com.railconnect.entity.Coach;
import com.railconnect.entity.Train;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.TrainRepository;
import com.railconnect.train.mapper.CoachMapper;
import com.railconnect.journey.dtorequestresponse.CoachRequest;
import com.railconnect.journey.dtorequestresponse.CoachResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;
    private final TrainRepository trainRepository;
    private final CoachMapper coachMapper;
    private final SeatGenerationService seatGenerationService;

    public CoachServiceImpl(CoachRepository coachRepository,
                            TrainRepository trainRepository,
                            CoachMapper coachMapper,
                            SeatGenerationService seatGenerationService) {
        this.coachRepository = coachRepository;
        this.trainRepository = trainRepository;
        this.coachMapper = coachMapper;
        this.seatGenerationService = seatGenerationService;
    }

    @Override
    @Transactional
    public CoachResponse addCoachToTrain(CoachRequest request) {
        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));

        if (coachRepository.existsByTrainIdAndCoachNumber(request.getTrainId(), request.getCoachNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Coach number " + request.getCoachNumber() + " already exists on this train");
        }

        Coach coach = coachMapper.toEntity(request);
        coach.setTrain(train);
        coach.setIsActive(true);
        coach.setSeats(seatGenerationService.generateCoachSeats(coach));
        Coach savedCoach = coachRepository.save(coach);
        return coachMapper.toResponse(savedCoach);
    }

    @Override
    @Transactional
    public CoachResponse updateCoach(Long coachId, CoachRequest request) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach configuration not found"));

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));

        if (coachRepository.existsByTrainIdAndCoachNumberAndIdNot(request.getTrainId(), request.getCoachNumber(), coachId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Coach number " + request.getCoachNumber() + " already exists on this train");
        }

        coach.setTrain(train);
        coach.setCoachNumber(request.getCoachNumber());
        coach.setCoachType(request.getCoachType());
        coach.setSeatCount(request.getSeatCount());
        coach.setPosition(request.getPosition());
        coach.setIsActive(true);

        coach.getSeats().clear();
        coach.setSeats(seatGenerationService.generateCoachSeats(coach));

        Coach savedCoach = coachRepository.save(coach);
        return coachMapper.toResponse(savedCoach);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoachResponse> getCoachesByTrain(Long trainId) {
        return coachRepository.findByTrainId(trainId).stream()
                .sorted(Comparator.comparing(Coach::getPosition)) // Sorts cleanly via memory stream logic
                .map(coachMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeCoach(Long coachId) {
        if (!coachRepository.existsById(coachId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach configuration not found");
        }
        coachRepository.deleteById(coachId);
    }

    @Override
    @Transactional
    public CoachResponse regenerateSeats(Long coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach configuration not found"));

        // orphanRemoval=true on Coach.seats means clearing + re-adding deletes the old rows.
        coach.getSeats().clear();
        coach.setSeats(seatGenerationService.generateCoachSeats(coach));

        Coach savedCoach = coachRepository.save(coach);
        return coachMapper.toResponse(savedCoach);
    }
}