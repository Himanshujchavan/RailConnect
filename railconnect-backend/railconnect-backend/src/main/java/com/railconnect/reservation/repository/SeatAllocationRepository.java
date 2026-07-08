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
}