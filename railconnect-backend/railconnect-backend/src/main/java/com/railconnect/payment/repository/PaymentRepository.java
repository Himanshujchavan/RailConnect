package com.railconnect.payment.repository;

import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionRef(String transactionRef);

    /**
     * Every payment that was actually settled (paid, and optionally later refunded/partially
     * refunded) within a paid-at window. Used by Phase 9's revenue report.
     */
    List<Payment> findByStatusInAndPaidAtBetween(List<PaymentStatus> statuses, LocalDateTime from, LocalDateTime to);
}
