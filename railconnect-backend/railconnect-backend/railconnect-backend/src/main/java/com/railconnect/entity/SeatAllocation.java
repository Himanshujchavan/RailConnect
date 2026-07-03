package com.railconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_allocations")
public class SeatAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "seat_id")
    public Seat seat;

    @ManyToOne
    @JoinColumn(name = "booking_passenger_id")
    public BookingPassenger bookingPassenger;

    public SeatAllocation() {}
}
