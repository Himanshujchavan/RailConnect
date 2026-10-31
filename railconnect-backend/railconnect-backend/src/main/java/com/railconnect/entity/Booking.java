package com.railconnect.entity;

import com.railconnect.common.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;


    private LocalDate journeyDate;
    private Double totalFare;
    private String pnrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Raw FK columns — managed by the entity directly for writes
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "coach_id")
    private Long coachId;

    // relationships to schedule and coach for easier access in controllers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", insertable = false, updatable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", insertable = false, updatable = false)
    private Coach coach;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingPassenger> bookingPassengers;
}
