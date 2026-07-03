package com.railconnect.reservation.repository;

import com.railconnect.entity.PNR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PNRRepository extends JpaRepository<PNR, Long> {
}