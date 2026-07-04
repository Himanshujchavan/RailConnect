package com.railconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "seats",
       uniqueConstraints = @UniqueConstraint(columnNames = {"coach_id", "seat_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @NotNull
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // e.g., 1, 2, 3...

    @NotBlank
    @Column(name = "berth_type", nullable = false)
    private String berthType; // LOWER, UPPER, MIDDLE, SIDE LOWER, SIDE UPPER

    @NotBlank
    @Column(name = "seat_label", nullable = false)
    private String seatLabel; // e.g., "1 LB", "2 UB", "7 SL"
}