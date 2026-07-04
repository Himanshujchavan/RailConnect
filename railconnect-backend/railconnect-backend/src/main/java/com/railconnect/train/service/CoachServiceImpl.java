package com.railconnect.train.service;

import com.railconnect.entity.Coach;
import com.railconnect.entity.Seat;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;
    private final TrainRepository trainRepository;
    private final CoachMapper coachMapper;

    public CoachServiceImpl(CoachRepository coachRepository, TrainRepository trainRepository, CoachMapper coachMapper) {
        this.coachRepository = coachRepository;
        this.trainRepository = trainRepository;
        this.coachMapper = coachMapper;
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

        List<Seat> generatedSeats = new ArrayList<>();

        // Indian Railways standard 8-berth layout sequence template (Sleeper/3AC)
        String[] berthPatterns = {"LOWER", "MIDDLE", "UPPER", "LOWER", "MIDDLE", "UPPER", "SIDE LOWER", "SIDE UPPER"};
        String[] labelSuffixes = {"LB", "MB", "UB", "LB", "MB", "UB", "SL", "SU"};

        for (int i = 1; i <= request.getSeatCount(); i++) {
            String berthType;
            String seatLabel;

            if (request.getCoachType().equalsIgnoreCase("AC 2 Tier")) {
                int remainder = i % 4;
                if (remainder == 1) { berthType = "LOWER"; seatLabel = i + " LB"; }
                else if (remainder == 2) { berthType = "UPPER"; seatLabel = i + " UB"; }
                else if (remainder == 3) { berthType = "SIDE LOWER"; seatLabel = i + " SL"; }
                else { berthType = "SIDE UPPER"; seatLabel = i + " SU"; }
            } else {
                int index = (i - 1) % 8;
                berthType = berthPatterns[index];
                seatLabel = i + " " + labelSuffixes[index];
            }

            Seat seat = Seat.builder()
                    .coach(coach)
                    .seatNumber(i)
                    .berthType(berthType)
                    .seatLabel(seatLabel)
                    .build();
            generatedSeats.add(seat);
        }

        coach.setSeats(generatedSeats);
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
}