package com.railconnect.train.repository;

import com.railconnect.entity.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {

    /**
     * Finds all actively booked seats for a particular train run on a specific date.
     */
    @Query("SELECT sa FROM SeatAllocation sa WHERE sa.schedule.id = :scheduleId " +
           "AND sa.journeyDate = :date AND sa.status = 'BOOKED'")
    List<SeatAllocation> findActiveAllocations(
            @Param("scheduleId") Long scheduleId, 
            @Param("date") LocalDate date
    );

    /**
     * Check if a specific physical seat is already booked for a specific journey date.
     */
    boolean existsBySeatIdAndScheduleIdAndJourneyDateAndStatus(
            Long seatId, Long scheduleId, LocalDate journeyDate, String status
    );
}
