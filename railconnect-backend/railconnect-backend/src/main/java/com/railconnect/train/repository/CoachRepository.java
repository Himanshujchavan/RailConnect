package com.railconnect.train.repository;

import com.railconnect.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    List<Coach> findByTrainId(Long trainId);
    boolean existsByTrainIdAndCoachNumber(Long trainId, String coachNumber);
}