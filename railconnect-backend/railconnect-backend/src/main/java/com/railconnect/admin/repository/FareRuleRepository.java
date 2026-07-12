package com.railconnect.admin.repository;

import com.railconnect.common.enums.CoachType;
import com.railconnect.entity.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FareRuleRepository extends JpaRepository<FareRule, Long> {
    Optional<FareRule> findByCoachType(CoachType coachType);
    boolean existsByCoachType(CoachType coachType);
}
