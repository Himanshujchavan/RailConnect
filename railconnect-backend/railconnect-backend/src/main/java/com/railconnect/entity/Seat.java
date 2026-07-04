package com.railconnect.entity;

import com.railconnect.common.enums.BerthType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "seats",
    uniqueConstraints = @UniqueConstraint(columnNames = {"coach_id", "seat_number"})
)
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
    private Integer seatNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "berth_type", nullable = false)
    private BerthType berthType;

    @NotNull
    @Column(name = "seat_label", nullable = false)
    private String seatLabel;
}