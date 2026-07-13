package com.railconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Admin-managed surcharge applied on a specific calendar date (Diwali, Christmas, New Year's
 * Eve, etc.) - Dynamic Pricing's Festival Pricing.
 */
@Entity
@Table(name = "festival_pricing_rules")
public class FestivalPricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true, nullable = false)
    public LocalDate date;

    public String festivalName;

    public Double surchargePercent;

    public FestivalPricingRule() {}
}
