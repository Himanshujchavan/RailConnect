package com.railconnect.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    public Train train;

    @OneToMany(mappedBy = "route")
    public List<Schedule> schedules;

    public Route() {}
}
