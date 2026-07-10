package com.railconnect.payment.service;

import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.common.enums.RefundStatus;
import com.railconnect.common.enums.TransactionStatus;
import com.railconnect.common.enums.TransactionType;
import com.railconnect.common.exception.RefundNotAllowedException;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.common.util.TransactionRefGenerator;
import com.railconnect.entity.Payment;
import com.railconnect.entity.PaymentLog;
import com.railconnect.entity.Refund;
import com.railconnect.entity.Transaction;
import com.railconnect.payment.dtorequestresponse.RefundRequest;
import com.railconnect.payment.dtorequestresponse.RefundResponse;
import com.railconnect.payment.mapper.RefundMapper;
import com.railconnect.payment.repository.PaymentLogRepository;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.payment.repository.RefundRepository;
import com.railconnect.payment.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Handles full and partial refunds against an already-settled {@link Payment}.
 * Refunds are simulated as processing synchronously and immediately (no external gateway call),
 * matching how {@link PaymentServiceImpl} simulates the payment side.
 */
@Service
public class RefundServiceImpl implements RefundService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final RefundMapper refundMapper;

    public RefundServiceImpl(PaymentRepository paymentRepository,
                              RefundRepository refundRepository,
                              TransactionRepository transactionRepository,
                              PaymentLogRepository paymentLogRepository,
                              RefundMapper refundMapper) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.transactionRepository = transactionRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.refundMapper = refundMapper;
    }

    @Override
    @Transactional
    public RefundResponse initiateRefund(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.paymentId()));

        if (payment.status != PaymentStatus.PAID && payment.status != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new RefundNotAllowedException("Only paid payments can be refunded.");
        }

        double alreadyRefunded = payment.refundedAmount != null ? payment.refundedAmount : 0.0;
        double refundable = payment.amount - alreadyRefunded;
        double refundAmount = request.amount() != null ? request.amount() : refundable;

        if (refundAmount <= 0 || refundAmount > refundable) {
            throw new RefundNotAllowedException(
                    "Refund amount must be greater than zero and no more than the refundable balance of " + refundable);
        }

        Refund refund = new Refund();
        refund.payment = payment;
        refund.amount = refundAmount;
        refund.reason = request.reason();
        refund.status = RefundStatus.PROCESSED;
        refund.createdAt = LocalDateTime.now();
        refund.processedAt = LocalDateTime.now();
        refund = refundRepository.save(refund);

        payment.refundedAmount = alreadyRefunded + refundAmount;
        payment.status = payment.refundedAmount >= payment.amount
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED;
        paymentRepository.save(payment);

        Transaction transaction = new Transaction();
        transaction.payment = payment;
        transaction.transactionRef = TransactionRefGenerator.generate();
        transaction.type = TransactionType.REFUND;
        transaction.status = TransactionStatus.SUCCESS;
        transaction.amount = refundAmount;
        transaction.createdAt = LocalDateTime.now();
        transactionRepository.save(transaction);

        PaymentLog paymentLog = new PaymentLog();
        paymentLog.payment = payment;
        paymentLog.action = "REFUND_PROCESSED";
        paymentLog.details = "Refunded " + refundAmount + " (reason: " + request.reason() + ")";
        paymentLog.createdAt = LocalDateTime.now();
        paymentLogRepository.save(paymentLog);

        return refundMapper.toResponse(refund);
    }
}
