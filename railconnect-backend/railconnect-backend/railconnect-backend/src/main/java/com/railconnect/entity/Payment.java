package com.railconnect.entity;

import com.railconnect.common.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Double amount;
    public LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    public PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "booking_id")
    public Booking booking;

    public Payment() {}
}
