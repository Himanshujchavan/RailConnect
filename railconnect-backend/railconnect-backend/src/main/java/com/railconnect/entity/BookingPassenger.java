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

    // LINK ADDED: Connects this specific booking passenger ticket to their allocated seat record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_allocation_id")
    private SeatAllocation seatAllocation;

    public BookingPassenger() {}

    // Getter and Setter for the new relationship mapping
    public SeatAllocation getSeatAllocation() {
        return seatAllocation;
    }

    public void setSeatAllocation(SeatAllocation seatAllocation) {
        this.seatAllocation = seatAllocation;
    }
}