package com.railconnect.realtime.event;

import com.railconnect.common.enums.TrainStatus;

/**
 * Published whenever a train's operational status changes (ON_TIME, DELAYED, CANCELLED, ...) -
 * backs Real-Time Features' "Live Train Status".
 */
public record TrainStatusChangedEvent(Long trainId, TrainStatus status) {
}
