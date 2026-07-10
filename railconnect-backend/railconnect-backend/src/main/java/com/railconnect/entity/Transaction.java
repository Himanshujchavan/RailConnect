package com.railconnect.entity;

import com.railconnect.common.enums.TransactionStatus;
import com.railconnect.common.enums.TransactionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable audit trail of every attempt made against the payment gateway
 * (both payment and refund attempts). Payment/Refund rows hold current state;
 * Transaction rows hold history.
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    public Payment payment;

    @Column(unique = true)
    public String transactionRef;

    @Enumerated(EnumType.STRING)
    public TransactionType type;

    @Enumerated(EnumType.STRING)
    public TransactionStatus status;

    public Double amount;

    // Free-form note from the (simulated) gateway - failure reason, auth code, etc.
    public String gatewayResponse;

    public LocalDateTime createdAt;

    public Transaction() {}
}
