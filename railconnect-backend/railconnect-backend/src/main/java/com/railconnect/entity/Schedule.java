package com.railconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    public Route route;

    public LocalDateTime departure;
    public LocalDateTime arrival;

    public Schedule() {}
}
