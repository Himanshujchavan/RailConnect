package com.railconnect.repository;

import com.railconnect.entity.Seat;
import com.railconnect.common.enums.BerthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Seat entity with custom queries needed by the weighted seat allocation algorithm.
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find seats in a specific coach that match a given berth type and are not currently allocated for the
     * provided schedule and journey date.
     */
    @Query("""
            SELECT s FROM Seat s
            WHERE s.coach.id = :coachId
              AND s.berthType = :berthType
              AND s.id NOT IN (
                  SELECT sa.seat.id FROM com.railconnect.entity.SeatAllocation sa
                  WHERE sa.schedule.id = :scheduleId
                    AND sa.journeyDate = :journeyDate
                    AND sa.status IN ('BOOKED', 'HELD')
              )
            ORDER BY s.seatNumber ASC
            """)
    List<Seat> findAvailableSeatsByCoachAndBerth(@Param("coachId") Long coachId,
                                                  @Param("berthType") BerthType berthType,
                                                  @Param("scheduleId") Long scheduleId,
                                                  @Param("journeyDate") LocalDate journeyDate);

    /**
     * Find all available seats in a coach (any berth) that are not allocated for the given schedule/date.
     */
    @Query("""
            SELECT s FROM Seat s
            WHERE s.coach.id = :coachId
              AND s.id NOT IN (
                  SELECT sa.seat.id FROM com.railconnect.entity.SeatAllocation sa
                  WHERE sa.schedule.id = :scheduleId
                    AND sa.journeyDate = :journeyDate
                    AND sa.status IN ('BOOKED', 'HELD')
              )
            ORDER BY s.seatNumber ASC
            """)
    List<Seat> findAvailableSeatsByCoach(@Param("coachId") Long coachId,
                                         @Param("scheduleId") Long scheduleId,
                                         @Param("journeyDate") LocalDate journeyDate);

    /**
     * Find available seats across all coaches that satisfy quota flags (ladies, senior, divyang) and optional berth.
     * Pass null for any flag you do not want to filter on.
     */
    @Query("""
            SELECT s FROM Seat s
            WHERE (:berthType IS NULL OR s.berthType = :berthType)
              AND (:isLadiesSeat IS NULL OR s.isLadiesSeat = :isLadiesSeat)
              AND (:isSeniorSeat IS NULL OR s.isSeniorSeat = :isSeniorSeat)
              AND (:isDivyangSeat IS NULL OR s.isDivyangSeat = :isDivyangSeat)
              AND s.id NOT IN (
                  SELECT sa.seat.id FROM com.railconnect.entity.SeatAllocation sa
                  WHERE sa.schedule.id = :scheduleId
                    AND sa.journeyDate = :journeyDate
                    AND sa.status IN ('BOOKED', 'HELD')
              )
            ORDER BY s.coach.id ASC, s.seatNumber ASC
            """)
    List<Seat> findAvailableSeatsWithQuota(@Param("scheduleId") Long scheduleId,
                                           @Param("journeyDate") LocalDate journeyDate,
                                           @Param("berthType") BerthType berthType,
                                           @Param("isLadiesSeat") Boolean isLadiesSeat,
                                           @Param("isSeniorSeat") Boolean isSeniorSeat,
                                           @Param("isDivyangSeat") Boolean isDivyangSeat);
}
