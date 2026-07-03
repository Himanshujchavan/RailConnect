package com.railconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_passengers")
public class BookingPassenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    public Booking booking;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    public Passenger passenger;

    public BookingPassenger() {}
}
