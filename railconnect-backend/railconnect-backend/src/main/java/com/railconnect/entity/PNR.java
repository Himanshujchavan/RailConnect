package com.railconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pnrs")
public class PNR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String code;

    @OneToOne
    @JoinColumn(name = "booking_id")
    public Booking booking;

    public PNR() {}
}
