package com.railconnect.reservation.repository;

import com.railconnect.entity.BookingPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingPassengerRepository extends JpaRepository<BookingPassenger, Long> {
}