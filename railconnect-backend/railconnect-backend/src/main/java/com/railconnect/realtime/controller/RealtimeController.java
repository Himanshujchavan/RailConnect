package com.railconnect.realtime.controller;

import com.railconnect.realtime.listener.RealtimeEventListener;
import com.railconnect.realtime.service.RealtimeSnapshotService;
import com.railconnect.realtime.service.SseEmitterRegistry;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;

/**
 * Real-Time Features, served over Server-Sent Events (one-way server -> client push over plain
 * HTTP - no WebSocket handshake or message broker needed). Each endpoint sends an immediate
 * snapshot on connect, then pushes again whenever the underlying data actually changes
 * ({@code SeatAllocationServiceImpl}, {@code CancellationServiceImpl}, and
 * {@code TrainServiceImpl} publish the events that drive this).
 * <p>
 * "Real-time Booking Updates" isn't a separate endpoint - a booking or cancellation is exactly
 * what changes seat availability, so it rides the same seat-availability stream below.
 */
@RestController
@RequestMapping("/api/v1/realtime")
public class RealtimeController {

    private final SseEmitterRegistry sseEmitterRegistry;
    private final RealtimeSnapshotService realtimeSnapshotService;

    public RealtimeController(SseEmitterRegistry sseEmitterRegistry,
                               RealtimeSnapshotService realtimeSnapshotService) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.realtimeSnapshotService = realtimeSnapshotService;
    }

    @GetMapping("/seat-availability/{scheduleId}/{journeyDate}")
    public SseEmitter streamSeatAvailability(
            @PathVariable Long scheduleId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate) {

        SseEmitter emitter = sseEmitterRegistry.subscribe(
                RealtimeEventListener.seatAvailabilityTopic(scheduleId, journeyDate));

        sendInitialSnapshot(emitter, "seat-availability",
                () -> realtimeSnapshotService.buildSeatAvailabilitySnapshot(scheduleId, journeyDate));

        return emitter;
    }

    @GetMapping("/train-status/{trainId}")
    public SseEmitter streamTrainStatus(@PathVariable Long trainId) {
        SseEmitter emitter = sseEmitterRegistry.subscribe(RealtimeEventListener.trainStatusTopic(trainId));

        sendInitialSnapshot(emitter, "train-status",
                () -> realtimeSnapshotService.buildTrainStatusSnapshot(trainId));

        return emitter;
    }

    private void sendInitialSnapshot(SseEmitter emitter, String eventName,
                                      java.util.function.Supplier<Object> snapshotSupplier) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(snapshotSupplier.get()));
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
    }
}
