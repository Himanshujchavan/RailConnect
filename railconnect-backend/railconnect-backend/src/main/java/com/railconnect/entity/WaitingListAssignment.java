package com.railconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_list_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitingListAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @NotNull
    @Column(name = "journey_date", nullable = false)
    private LocalDate journeyDate;

    @NotNull
    @Column(name = "booking_passenger_id", nullable = false)
    private Long bookingPassengerId;

    @NotBlank
    @Column(name = "queue_type", nullable = false)
    private String queueType; // "RAC" or "WL"

    @NotNull
    @Column(name = "priority_number", nullable = false)
    private Integer priorityNumber; // Strict sequential FIFO number (1, 2, 3...)

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotBlank
    @Column(name = "current_status", nullable = false)
    private String currentStatus; // "ACTIVE", "UPGRADED", "CANCELLED"
}