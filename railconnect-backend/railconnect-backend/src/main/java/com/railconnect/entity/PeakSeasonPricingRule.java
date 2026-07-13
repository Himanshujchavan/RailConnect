package com.railconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Admin-managed surcharge applied across a date range (summer holidays, year-end travel
 * rush, etc.) - Dynamic Pricing's Peak Season Pricing. When a journey date falls inside more
 * than one overlapping rule, the highest surcharge among them wins (no stacking).
 */
@Entity
@Table(name = "peak_season_pricing_rules")
public class PeakSeasonPricingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String seasonName;

    @Column(nullable = false)
    public LocalDate startDate;

    @Column(nullable = false)
    public LocalDate endDate;

    public Double surchargePercent;

    public PeakSeasonPricingRule() {}
}
