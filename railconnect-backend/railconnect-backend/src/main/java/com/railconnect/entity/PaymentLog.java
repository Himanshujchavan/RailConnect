package com.railconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Append-only log of everything that happened to a payment - useful for support/debugging
 * and for reconstructing a timeline independent of the current Payment/Transaction state.
 */
@Entity
@Table(name = "payment_logs")
public class PaymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    public Payment payment;

    // e.g. INITIATED, SUCCESS, FAILURE, REFUND_REQUESTED, REFUND_PROCESSED
    public String action;

    public String details;

    public LocalDateTime createdAt;

    public PaymentLog() {}
}
