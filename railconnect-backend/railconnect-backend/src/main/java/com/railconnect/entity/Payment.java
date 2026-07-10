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
    public LocalDateTime createdAt;

    // Reference shown to the user / passed to the (simulated) payment gateway.
    @Column(unique = true)
    public String transactionRef;

    // e.g. CARD, UPI, NETBANKING, WALLET
    public String paymentMethod;

    // Running total of everything refunded so far against this payment.
    public Double refundedAmount = 0.0;

    @Enumerated(EnumType.STRING)
    public PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "booking_id")
    public Booking booking;

    public Payment() {}
}
