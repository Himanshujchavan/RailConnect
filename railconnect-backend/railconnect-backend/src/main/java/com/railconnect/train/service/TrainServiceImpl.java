package com.railconnect.train.service;

import com.railconnect.common.enums.BookingStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;
import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationType;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.service.NotificationService;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.train.dtorequestresponse.TrainRequest; // Matches your actual package spelling
import com.railconnect.train.dtorequestresponse.TrainResponse;
import com.railconnect.train.mapper.TrainMapper;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.train.repository.TrainRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainServiceImpl implements TrainService {

    private static final Logger logger = LoggerFactory.getLogger(TrainServiceImpl.class);

    private final TrainRepository trainRepository;
    private final TrainMapper trainMapper;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    public TrainServiceImpl(TrainRepository trainRepository,
                             TrainMapper trainMapper,
                             ScheduleRepository scheduleRepository,
                             BookingRepository bookingRepository,
                             NotificationService notificationService) {
        this.trainRepository = trainRepository;
        this.trainMapper = trainMapper;
        this.scheduleRepository = scheduleRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
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
        Train savedTrain = trainRepository.save(train);

        if (status == TrainStatus.DELAYED) {
            notifyPassengersOfDelay(savedTrain);
        }

        return trainMapper.toResponse(savedTrain);
    }

    /**
     * Notifies every passenger with a still-relevant (today or later, not cancelled) booking on
     * this train. One notification failure never stops the others - NotificationService already
     * isolates per-channel failures, and we isolate per-passenger failures here too.
     */
    private void notifyPassengersOfDelay(Train train) {
        List<Long> scheduleIds = scheduleRepository.findByTrainId(train.getId()).stream()
                .map(Schedule::getId)
                .toList();

        if (scheduleIds.isEmpty()) {
            return;
        }

        List<Booking> affectedBookings = bookingRepository.findByScheduleIdInAndJourneyDateGreaterThanEqualAndStatusIn(
                scheduleIds, LocalDate.now(),
                List.of(BookingStatus.CONFIRMED, BookingStatus.RAC, BookingStatus.WAITING_LIST));

        String details = "Train " + train.getNumber() + " (" + train.getName() + ") is running late.";

        for (Booking booking : affectedBookings) {
            try {
                notificationService.send(new NotificationSendRequest(
                        booking.user.id,
                        NotificationType.TRAIN_DELAYED,
                        List.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP),
                        details,
                        null,
                        null));
            } catch (Exception ex) {
                logger.warn("Failed to notify user {} of delay on train {}: {}",
                        booking.user.id, train.getNumber(), ex.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainResponse> getTrainsByType(TrainType type) {
        return trainRepository.findByType(type).stream()
                .map(trainMapper::toResponse)
                .collect(Collectors.toList());
    }
}