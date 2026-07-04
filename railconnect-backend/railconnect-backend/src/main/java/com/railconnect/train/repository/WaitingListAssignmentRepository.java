package com.railconnect.train.repository;

import com.railconnect.entity.WaitingListAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListAssignmentRepository extends JpaRepository<WaitingListAssignment, Long> {

    /**
     * Gets the next active passenger in queue based on FIFO priority order.
     */
    Optional<WaitingListAssignment> findFirstByScheduleIdAndJourneyDateAndQueueTypeAndCurrentStatusOrderByPriorityNumberAsc(
            Long scheduleId, LocalDate journeyDate, String queueType, String currentStatus);

    /**
     * Finds the maximum priority number currently assigned to calculate the next position in line.
     */
    @Query("SELECT COALESCE(MAX(w.priorityNumber), 0) FROM WaitingListAssignment w " +
           "WHERE w.schedule.id = :scheduleId AND w.journeyDate = :date AND w.queueType = :type")
    Integer findMaxPriorityNumber(
            @Param("scheduleId") Long scheduleId, 
            @Param("date") LocalDate date, 
            @Param("type") String type
    );

    List<WaitingListAssignment> findByScheduleIdAndJourneyDateAndCurrentStatus(
            Long scheduleId, LocalDate journeyDate, String currentStatus);
}