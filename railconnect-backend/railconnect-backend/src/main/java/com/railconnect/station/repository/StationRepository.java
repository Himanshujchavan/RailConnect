package com.railconnect.station.repository;

import com.railconnect.station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    boolean existsByStationCode(String stationCode);
    boolean existsByStationName(String stationName);
    
    // Case-insensitive autocomplete search checking against name, code, or city
    @Query("SELECT s FROM Station s WHERE " +
           "LOWER(s.stationName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.stationCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Station> searchStations(@Param("keyword") String keyword);
}