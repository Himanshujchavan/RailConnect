package com.railconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPassenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    // LINK ADDED: Connects this specific booking passenger ticket to their allocated seat record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_allocation_id")
    private SeatAllocation seatAllocation;

    // New fields for PNR response
    private String berthPreference;
    private String seatNumber;
    private String currentStatus;

    public SeatAllocation getSeatAllocation() {
        return seatAllocation;
    }

    public void setSeatAllocation(SeatAllocation seatAllocation) {
        this.seatAllocation = seatAllocation;
    }
}
