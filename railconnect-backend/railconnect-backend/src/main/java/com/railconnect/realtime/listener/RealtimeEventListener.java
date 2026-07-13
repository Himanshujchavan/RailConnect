package com.railconnect.realtime.listener;

import com.railconnect.realtime.dtorequestresponse.SeatAvailabilitySnapshotResponse;
import com.railconnect.realtime.dtorequestresponse.TrainStatusSnapshotResponse;
import com.railconnect.realtime.event.SeatAvailabilityChangedEvent;
import com.railconnect.realtime.event.TrainStatusChangedEvent;
import com.railconnect.realtime.service.RealtimeSnapshotService;
import com.railconnect.realtime.service.SseEmitterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Bridges domain events (fired synchronously by the services that cause them) to the SSE
 * subscribers who care. Listens after commit ({@link TransactionPhase#AFTER_COMMIT}) so a
 * subscriber never sees a seat-availability push for a booking that then rolled back.
 */
@Component
public class RealtimeEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeEventListener.class);

    private final SseEmitterRegistry sseEmitterRegistry;
    private final RealtimeSnapshotService realtimeSnapshotService;

    public RealtimeEventListener(SseEmitterRegistry sseEmitterRegistry,
                                  RealtimeSnapshotService realtimeSnapshotService) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.realtimeSnapshotService = realtimeSnapshotService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSeatAvailabilityChanged(SeatAvailabilityChangedEvent event) {
        try {
            SeatAvailabilitySnapshotResponse snapshot = realtimeSnapshotService.buildSeatAvailabilitySnapshot(
                    event.scheduleId(), event.journeyDate());
            sseEmitterRegistry.broadcast(seatAvailabilityTopic(event.scheduleId(), event.journeyDate()),
                    "seat-availability", snapshot);
        } catch (Exception ex) {
            logger.warn("Failed to broadcast seat-availability update for schedule {} on {}: {}",
                    event.scheduleId(), event.journeyDate(), ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTrainStatusChanged(TrainStatusChangedEvent event) {
        try {
            TrainStatusSnapshotResponse snapshot = realtimeSnapshotService.buildTrainStatusSnapshot(event.trainId());
            sseEmitterRegistry.broadcast(trainStatusTopic(event.trainId()), "train-status", snapshot);
        } catch (Exception ex) {
            logger.warn("Failed to broadcast train-status update for train {}: {}",
                    event.trainId(), ex.getMessage());
        }
    }

    public static String seatAvailabilityTopic(Long scheduleId, java.time.LocalDate journeyDate) {
        return "seat-availability:" + scheduleId + ":" + journeyDate;
    }

    public static String trainStatusTopic(Long trainId) {
        return "train-status:" + trainId;
    }
}
