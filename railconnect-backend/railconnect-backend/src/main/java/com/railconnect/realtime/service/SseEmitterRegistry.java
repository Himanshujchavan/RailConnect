package com.railconnect.realtime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of open SSE connections, keyed by an arbitrary topic string (e.g.
 * {@code "seat-availability:42:2026-08-01"} or {@code "train-status:7"}). Deliberately simple -
 * no external broker, no persistence, connections are lost on app restart - which is the right
 * tradeoff for a single-instance deployment. A multi-instance production deployment would need
 * to swap this for a shared pub/sub (Redis, etc.) so an event on one instance reaches
 * subscribers connected to another.
 */
@Component
public class SseEmitterRegistry {

    private static final Logger logger = LoggerFactory.getLogger(SseEmitterRegistry.class);
    private static final long EMITTER_TIMEOUT_MILLIS = 30L * 60 * 1000; // 30 minutes

    private final Map<String, List<SseEmitter>> emittersByTopic = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String topic) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT_MILLIS);
        List<SseEmitter> emitters = emittersByTopic.computeIfAbsent(topic, key -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);

        Runnable cleanup = () -> {
            List<SseEmitter> list = emittersByTopic.get(topic);
            if (list != null) {
                list.remove(emitter);
            }
        };
        emitter.onCompletion(cleanup::run);
        emitter.onTimeout(cleanup::run);
        emitter.onError(ex -> cleanup.run());

        return emitter;
    }

    public void broadcast(String topic, String eventName, Object data) {
        List<SseEmitter> emitters = emittersByTopic.get(topic);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException | IllegalStateException ex) {
                logger.debug("Dropping a dead SSE subscriber on topic {}: {}", topic, ex.getMessage());
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }
}
