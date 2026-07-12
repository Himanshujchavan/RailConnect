package com.railconnect.dashboard.repository;

import com.railconnect.entity.FavouriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRouteRepository extends JpaRepository<FavouriteRoute, Long> {
    List<FavouriteRoute> findByUserId(Long userId);
    boolean existsByUserIdAndRouteId(Long userId, Long routeId);
    Optional<FavouriteRoute> findByIdAndUserId(Long id, Long userId);
}
