package com.railconnect.entity;
import com.railconnect.station.entity.Station;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "route_stations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "stop_order"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @NotNull
    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder; // Sequence numbers: 1, 2, 3...

    @NotNull
    @Column(name = "distance_from_source", nullable = false)
    private Integer distanceFromSource; // Cumulative kilometers from origin
}