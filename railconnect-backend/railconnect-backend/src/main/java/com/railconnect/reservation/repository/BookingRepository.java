package com.railconnect.reservation.repository;

import com.railconnect.common.enums.BookingStatus;
import com.railconnect.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPnrCode(String pnrCode);

    /**
     * Used to find every still-relevant passenger on a train when it's marked DELAYED, so
     * they can be notified. "Still relevant" = today or later, and not already cancelled.
     */
    List<Booking> findByScheduleIdInAndJourneyDateGreaterThanEqualAndStatusIn(
            List<Long> scheduleIds, LocalDate journeyDate, List<BookingStatus> statuses);
}