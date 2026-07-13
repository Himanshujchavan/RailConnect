package com.railconnect.reservation.repository;

import com.railconnect.entity.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

@Repository
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sa
        FROM SeatAllocation sa
        WHERE sa.schedule.id = :scheduleId
        AND sa.journeyDate = :journeyDate
        AND sa.status IN ('BOOKED', 'HELD')
    """)
    List<SeatAllocation> findOccupiedSeats(
            @Param("scheduleId") Long scheduleId,
            @Param("journeyDate") LocalDate journeyDate
    );

    // --- Dynamic Pricing ---

    /**
     * How many seats are currently booked in this specific coach, on this schedule/date.
     * Used by {@code DynamicPricingService} to compute occupancy-based surcharge/discount.
     */
    long countBySeat_Coach_IdAndSchedule_IdAndJourneyDateAndStatus(
            Long coachId, Long scheduleId, LocalDate journeyDate, String status);

    // --- Phase 9 — Reporting ---

    long countByJourneyDateBetweenAndStatus(LocalDate from, LocalDate to, String status);

    @Query("""
        SELECT s.coach.coachType, COUNT(sa)
        FROM SeatAllocation sa
        JOIN sa.seat s
        WHERE sa.journeyDate BETWEEN :from AND :to
        AND sa.status = :status
        GROUP BY s.coach.coachType
    """)
    List<Object[]> countBookedByCoachType(@Param("from") LocalDate from,
                                          @Param("to") LocalDate to,
                                          @Param("status") String status);
}