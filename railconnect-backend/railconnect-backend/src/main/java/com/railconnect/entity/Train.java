package com.railconnect.entity;

import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus; // Assuming you have a status enum
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "train_number", unique = true, nullable = false, length = 10)
    private String number;

    @NotBlank
    @Column(name = "train_name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "train_type", nullable = false)
    private TrainType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrainStatus status = TrainStatus.ACTIVE; // Defaults to active on creation

    @Column(name = "route_id", nullable = false)
    private Long routeId; // Connects this train to its physical station route sequence

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coach> coaches = new ArrayList<>();
}
