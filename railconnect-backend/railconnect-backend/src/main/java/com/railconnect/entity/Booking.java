package com.railconnect.entity;

import com.railconnect.common.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    public BookingStatus status;

    public Long scheduleId;
    public Long coachId;
    public LocalDate journeyDate;
    public Double totalFare;
    public String pnrCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<BookingPassenger> bookingPassengers;

    public Booking() {}
}
