package com.railconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coaches", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"train_id", "coach_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EXPLICIT PUBLIC GETTER TO FIX ACCESSIBILITY IN MAPPERS
    public Long getId() {
        return this.id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @NotBlank
    @Column(name = "coach_number", nullable = false)
    private String coachNumber;

    @NotBlank
    @Column(name = "coach_type", nullable = false)
    private String coachType;

    @NotNull
    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @NotNull
    @Column(name = "position", nullable = false)
    private Integer position;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();
}