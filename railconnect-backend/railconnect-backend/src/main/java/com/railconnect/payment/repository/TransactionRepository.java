package com.railconnect.payment.repository;

import com.railconnect.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
    Optional<Transaction> findByTransactionRef(String transactionRef);
}
