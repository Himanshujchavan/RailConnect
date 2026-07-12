package com.railconnect.train.repository;

import com.railconnect.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    List<Coach> findByTrainId(Long trainId);
    boolean existsByTrainIdAndCoachNumber(Long trainId, String coachNumber);
    boolean existsByTrainIdAndCoachNumberAndIdNot(Long trainId, String coachNumber, Long id);

    /**
     * Total seat capacity per coach type, across every active coach in the fleet. Used by
     * Phase 9's coach-utilization report as the denominator (capacity available per day).
     */
    @Query("SELECT c.coachType, SUM(c.seatCount) FROM Coach c WHERE c.isActive = true GROUP BY c.coachType")
    List<Object[]> sumSeatCountByCoachType();
}