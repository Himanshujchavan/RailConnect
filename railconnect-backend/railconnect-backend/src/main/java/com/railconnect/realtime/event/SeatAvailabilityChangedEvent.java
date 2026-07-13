package com.railconnect.realtime.event;

import java.time.LocalDate;

/**
 * Published whenever a booking, cancellation, or waiting-list promotion changes how many seats
 * are free on a schedule/date. Real-Time Features' "Live Seat Availability" and "Real-time
 * Booking Updates" both ride on this single event - a new booking IS the "real-time booking
 * update" that live availability needs to react to.
 */
public record SeatAvailabilityChangedEvent(Long scheduleId, LocalDate journeyDate) {
}
