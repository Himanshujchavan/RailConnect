package com.railconnect.entity;

import com.railconnect.common.enums.SeatStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String seatNumber;

    @Enumerated(EnumType.STRING)
    public SeatStatus status;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    public Coach coach;

    public Seat() {}
}
