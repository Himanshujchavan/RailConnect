package com.railconnect.train.repository;

import com.railconnect.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Finds all schedules assigned to a specific train.
     */
    List<Schedule> findByTrainId(Long trainId);

    /**
     * Finds all schedules associated with a physical track route layout.
     */
    List<Schedule> findByRouteId(Long routeId);

    /**
     * Finds active schedules running on a given day of the week (e.g., 'MONDAY')
     */
    @Query("SELECT s FROM Schedule s JOIN s.operatingDays od WHERE od = :day")
    List<Schedule> findByOperatingDay(@Param("day") String day);

    /**
     * Advanced Search: Finds all schedules connecting a source route layout on a specific day.
     */
    @Query("SELECT s FROM Schedule s " +
           "JOIN s.operatingDays od " +
           "WHERE s.route.id = :routeId AND od = :day")
    List<Schedule> findByRouteAndOperatingDay(
            @Param("routeId") Long routeId, 
            @Param("day") String day
    );
}