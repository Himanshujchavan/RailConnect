package com.railconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seat_allocations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"seat_id", "schedule_id", "journey_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return this.id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @NotNull
    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate; // e.g., 2026-08-12

    @Column(name = "booking_passenger_id") 
    private Long bookingPassengerId; // Nullable if seat is temporarily held/blocked during checkout

    @NotNull
    @Column(name = "allocated_at", nullable = false)
    private LocalDateTime allocatedAt;

    @NotBlank
    @Column(name = "status", nullable = false)
    private String status; // BOOKED, CANCELLED, HELD
}