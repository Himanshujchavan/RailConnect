package com.railconnect.pricing.repository;

import com.railconnect.entity.FestivalPricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FestivalPricingRuleRepository extends JpaRepository<FestivalPricingRule, Long> {
    Optional<FestivalPricingRule> findByDate(LocalDate date);
    List<FestivalPricingRule> findAllByOrderByDateAsc();
}
