package com.railconnect.train.repository;

import com.railconnect.entity.Train;
import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    /**
     * Finds a train by its unique operational number (e.g., "12260").
     */
    Optional<Train> findByNumber(String number);

    /**
     * Checks if a train number already exists in the system (Crucial for Create CRUD validations).
     */
    boolean existsByNumber(String number);

    /**
     * Retrieves all trains matching a specific category (e.g., RAJDHANI, EXPRESS).
     */
    List<Train> findByType(TrainType type);

    /**
     * Retrieves all trains under a specific operational status (e.g., DELAYED, CANCELLED).
     */
    List<Train> findByStatus(TrainStatus status);

    /**
     * Finds all trains assigned to run along a specific physical route layout.
     */
    List<Train> findByRouteId(Long routeId);
}