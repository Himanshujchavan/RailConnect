package com.railconnect.common.health;

import com.railconnect.reservation.repository.BookingRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Phase 10 — Health Check.
 * <p>
 * Spring Boot Actuator already auto-registers health indicators for the DB connection pool,
 * Redis, and mail (since their starters are on the classpath) - this adds one small
 * app-specific check on top: that a real query against the bookings table succeeds, which is a
 * closer proxy for "can this app actually serve booking traffic" than a bare connection check.
 * Shows up as the {@code bookingSystem} entry under {@code GET /actuator/health} (with
 * {@code show-details} enabled).
 */
@Component
public class BookingSystemHealthIndicator implements HealthIndicator {

    private final BookingRepository bookingRepository;

    public BookingSystemHealthIndicator(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Health health() {
        try {
            long totalBookings = bookingRepository.count();
            return Health.up()
                    .withDetail("totalBookings", totalBookings)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
