package com.railconnect.entity;

import com.railconnect.common.enums.BookingStatus;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @OneToMany(mappedBy = "booking")
    public List<BookingPassenger> bookingPassengers;

    public Booking() {}
}
