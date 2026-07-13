package com.railconnect.realtime.service;

import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.entity.Coach;
import com.railconnect.entity.SeatAllocation;
import com.railconnect.entity.Train;
import com.railconnect.realtime.dtorequestresponse.CoachAvailabilitySnapshot;
import com.railconnect.realtime.dtorequestresponse.SeatAvailabilitySnapshotResponse;
import com.railconnect.realtime.dtorequestresponse.TrainStatusSnapshotResponse;
import com.railconnect.reservation.repository.SeatAllocationRepository;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.ScheduleRepository;
import com.railconnect.train.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds the current-state payloads Real-Time Features pushes over SSE. Kept separate from
 * {@link SseEmitterRegistry} (pure transport) and the event listener (wiring), so this snapshot
 * logic can also be unit tested or reused without any SSE machinery involved.
 */
@Service
public class RealtimeSnapshotService {

    private final ScheduleRepository scheduleRepository;
    private final CoachRepository coachRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final TrainRepository trainRepository;

    public RealtimeSnapshotService(ScheduleRepository scheduleRepository,
                                    CoachRepository coachRepository,
                                    SeatAllocationRepository seatAllocationRepository,
                                    TrainRepository trainRepository) {
        this.scheduleRepository = scheduleRepository;
        this.coachRepository = coachRepository;
        this.seatAllocationRepository = seatAllocationRepository;
        this.trainRepository = trainRepository;
    }

    @Transactional(readOnly = true)
    public SeatAvailabilitySnapshotResponse buildSeatAvailabilitySnapshot(Long scheduleId, LocalDate journeyDate) {
        var schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));

        List<Coach> coaches = coachRepository.findByTrainId(schedule.getTrain().getId()).stream()
                .filter(coach -> Boolean.TRUE.equals(coach.getIsActive()))
                .toList();

        Set<Long> occupiedSeatIds = seatAllocationRepository.findOccupiedSeats(scheduleId, journeyDate).stream()
                .map(SeatAllocation::getSeat)
                .map(seat -> seat.getId())
                .collect(Collectors.toSet());

        List<CoachAvailabilitySnapshot> coachSnapshots = coaches.stream()
                .map(coach -> {
                    long occupied = coach.getSeats().stream()
                            .filter(seat -> occupiedSeatIds.contains(seat.getId()))
                            .count();
                    int total = coach.getSeatCount() != null ? coach.getSeatCount() : 0;
                    return new CoachAvailabilitySnapshot(
                            coach.getId(), coach.getCoachNumber(), coach.getCoachType(),
                            total, Math.max(total - (int) occupied, 0));
                })
                .toList();

        return new SeatAvailabilitySnapshotResponse(scheduleId, journeyDate, coachSnapshots);
    }

    @Transactional(readOnly = true)
    public TrainStatusSnapshotResponse buildTrainStatusSnapshot(Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException("Train", "id", trainId));
        return new TrainStatusSnapshotResponse(train.getId(), train.getNumber(), train.getStatus());
    }
}
