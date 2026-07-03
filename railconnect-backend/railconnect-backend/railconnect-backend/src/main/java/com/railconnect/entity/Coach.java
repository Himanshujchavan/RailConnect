package com.railconnect.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "coaches")
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String code;
    public String name;

    @ManyToOne
    @JoinColumn(name = "train_id")
    public Train train;

    @OneToMany(mappedBy = "coach")
    public List<Seat> seats;

    public Coach() {}
}
