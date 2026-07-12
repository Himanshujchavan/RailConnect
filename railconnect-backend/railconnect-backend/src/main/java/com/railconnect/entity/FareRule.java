package com.railconnect.entity;

import com.railconnect.common.enums.CoachType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Admin-managed override for the per-km rate {@link com.railconnect.common.util.FareCalculator}
 * uses for a given coach type. When no rule exists for a coach type, FareCalculator's built-in
 * default rate is used instead - so seeding this table is optional, not required for the system
 * to function.
 */
@Entity
@Table(name = "fare_rules")
public class FareRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    public CoachType coachType;

    public Double ratePerKm;

    public LocalDateTime updatedAt;

    public FareRule() {}
}
