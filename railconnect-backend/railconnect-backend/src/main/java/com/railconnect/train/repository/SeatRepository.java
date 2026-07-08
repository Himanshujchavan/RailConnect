package com.railconnect.train.repository;

import com.railconnect.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("trainSeatRepository")
public interface SeatRepository extends JpaRepository<Seat, Long> {
	List<Seat> findByCoachId(Long coachId);
	List<Seat> findByCoachTrainId(Long trainId);
}