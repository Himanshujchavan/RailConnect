package com.railconnect.train.repository;

import com.railconnect.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    /**
     * Finds a route by its unique custom descriptive name.
     */
    Optional<Route> findByRouteName(String routeName);

    /**
     * Checks if a route name is already taken before creating/updating.
     */
    boolean existsByRouteName(String routeName);

    /**
     * Same uniqueness check as {@link #existsByRouteName}, but excludes the route being
     * updated so renaming a route to its own current name doesn't false-positive as a conflict.
     */
    boolean existsByRouteNameAndIdNot(String routeName, Long id);

    /**
     * Finds all routes that originate from a specific station.
     */
    List<Route> findBySourceStationId(Long sourceStationId);

    /**
     * Finds all routes that terminate at a specific station.
     */
    List<Route> findByDestinationStationId(Long destinationStationId);

    /**
     * Highly Advanced Query: Finds all routes running through BOTH an origin and a destination station, 
     * ensuring that the destination comes AFTER the origin in the sequence loop.
     */
    @Query("SELECT r FROM Route r " +
           "JOIN r.routeStations rs1 " +
           "JOIN r.routeStations rs2 " +
           "WHERE rs1.station.id = :originId " +
           "AND rs2.station.id = :destinationId " +
           "AND rs1.stopOrder < rs2.stopOrder")
    List<Route> findRoutesBetweenStations(
            @Param("originId") Long originId, 
            @Param("destinationId") Long destinationId
    );
}