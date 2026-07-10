package com.railconnect.entity;

import com.railconnect.common.enums.RefundStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    public Payment payment;

    public Double amount;
    public String reason;

    @Enumerated(EnumType.STRING)
    public RefundStatus status;

    public LocalDateTime createdAt;
    public LocalDateTime processedAt;

    public Refund() {}
}
