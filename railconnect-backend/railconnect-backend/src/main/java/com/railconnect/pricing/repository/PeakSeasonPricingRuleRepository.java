package com.railconnect.pricing.repository;

import com.railconnect.entity.PeakSeasonPricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeakSeasonPricingRuleRepository extends JpaRepository<PeakSeasonPricingRule, Long> {

    @Query("SELECT p FROM PeakSeasonPricingRule p WHERE :date BETWEEN p.startDate AND p.endDate")
    List<PeakSeasonPricingRule> findActiveOn(@Param("date") LocalDate date);

    List<PeakSeasonPricingRule> findAllByOrderByStartDateAsc();
}
