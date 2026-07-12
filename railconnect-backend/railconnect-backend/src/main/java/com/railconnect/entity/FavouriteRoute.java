package com.railconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favourite_routes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "route_id"}))
public class FavouriteRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne
    @JoinColumn(name = "route_id")
    public Route route;

    public LocalDateTime createdAt;

    public FavouriteRoute() {}
}
