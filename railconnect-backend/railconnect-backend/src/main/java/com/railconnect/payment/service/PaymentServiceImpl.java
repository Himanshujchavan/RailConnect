package com.railconnect.payment.service;

import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.common.enums.TransactionStatus;
import com.railconnect.common.enums.TransactionType;
import com.railconnect.common.exception.PaymentFailedException;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.common.util.TransactionRefGenerator;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.entity.PaymentLog;
import com.railconnect.entity.Refund;
import com.railconnect.entity.Transaction;
import com.railconnect.payment.dtorequestresponse.InvoiceResponse;
import com.railconnect.payment.dtorequestresponse.PaymentCallbackRequest;
import com.railconnect.payment.dtorequestresponse.PaymentHistoryResponse;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateRequest;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateResponse;
import com.railconnect.payment.dtorequestresponse.PaymentResponse;
import com.railconnect.payment.dtorequestresponse.RefundResponse;
import com.railconnect.payment.dtorequestresponse.TransactionResponse;
import com.railconnect.payment.mapper.PaymentMapper;
import com.railconnect.payment.mapper.RefundMapper;
import com.railconnect.payment.mapper.TransactionMapper;
import com.railconnect.payment.repository.PaymentLogRepository;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.payment.repository.RefundRepository;
import com.railconnect.payment.repository.TransactionRepository;
import com.railconnect.reservation.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 4 — Payment Module.
 * <p>
 * Simulates a payment-gateway integration: {@code initiate} creates a PENDING payment and hands
 * back a transaction reference, then the gateway (or, in tests/dev, the client directly) calls
 * back into {@code success}/{@code failure} to settle it. {@link Transaction} rows keep an
 * immutable history of every attempt; {@link PaymentLog} keeps a plain-text audit trail alongside it.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final TransactionMapper transactionMapper;
    private final RefundMapper refundMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               TransactionRepository transactionRepository,
                               PaymentLogRepository paymentLogRepository,
                               RefundRepository refundRepository,
                               BookingRepository bookingRepository,
                               PaymentMapper paymentMapper,
                               TransactionMapper transactionMapper,
                               RefundMapper refundMapper) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.refundRepository = refundRepository;
        this.bookingRepository = bookingRepository;
        this.paymentMapper = paymentMapper;
        this.transactionMapper = transactionMapper;
        this.refundMapper = refundMapper;
    }

    @Override
    @Transactional
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));

        Payment existing = paymentRepository.findByBookingId(booking.id).orElse(null);
        if (existing != null && existing.status == PaymentStatus.PAID) {
            throw new PaymentFailedException("This booking has already been paid for.");
        }
        if (existing != null && existing.status == PaymentStatus.PENDING) {
            // Idempotent: re-initiating an in-flight payment just hands back the same reference.
            return new PaymentInitiateResponse(existing.id, booking.id, existing.transactionRef,
                    existing.amount, existing.status);
        }

        Payment payment = existing != null ? existing : new Payment();
        payment.booking = booking;
        payment.amount = booking.totalFare;
        payment.paymentMethod = request.paymentMethod();
        payment.status = PaymentStatus.PENDING;
        payment.transactionRef = TransactionRefGenerator.generate();
        payment.createdAt = LocalDateTime.now();
        payment.refundedAmount = payment.refundedAmount != null ? payment.refundedAmount : 0.0;
        payment = paymentRepository.save(payment);

        Transaction transaction = new Transaction();
        transaction.payment = payment;
        transaction.transactionRef = payment.transactionRef;
        transaction.type = TransactionType.PAYMENT;
        transaction.status = TransactionStatus.PENDING;
        transaction.amount = payment.amount;
        transaction.createdAt = LocalDateTime.now();
        transactionRepository.save(transaction);

        log(payment, "INITIATED", "Payment initiated via " + request.paymentMethod());

        return new PaymentInitiateResponse(payment.id, booking.id, payment.transactionRef,
                payment.amount, payment.status);
    }

    @Override
    @Transactional
    public PaymentResponse handleSuccess(PaymentCallbackRequest request) {
        Payment payment = findByRef(request.transactionRef());
        Transaction transaction = findTransaction(request.transactionRef());

        if (payment.status == PaymentStatus.PAID) {
            // Already settled - treat repeat gateway callbacks as a no-op instead of erroring.
            return paymentMapper.toResponse(payment);
        }
        if (payment.status == PaymentStatus.FAILED) {
            throw new PaymentFailedException("Cannot mark an already-failed payment as successful.");
        }

        payment.status = PaymentStatus.PAID;
        payment.paidAt = LocalDateTime.now();
        paymentRepository.save(payment);

        transaction.status = TransactionStatus.SUCCESS;
        transaction.gatewayResponse = request.gatewayReference();
        transactionRepository.save(transaction);

        log(payment, "SUCCESS", "Payment confirmed. Gateway ref: " + request.gatewayReference());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse handleFailure(PaymentCallbackRequest request) {
        Payment payment = findByRef(request.transactionRef());
        Transaction transaction = findTransaction(request.transactionRef());

        if (payment.status == PaymentStatus.PAID) {
            throw new PaymentFailedException("Cannot mark an already-paid payment as failed.");
        }

        payment.status = PaymentStatus.FAILED;
        paymentRepository.save(payment);

        transaction.status = TransactionStatus.FAILED;
        transaction.gatewayResponse = request.gatewayReference();
        transactionRepository.save(transaction);

        log(payment, "FAILURE", "Payment failed. Reason: " + request.gatewayReference());

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentHistoryResponse getHistory(Long bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));

        List<TransactionResponse> transactions = transactionRepository
                .findByPaymentIdOrderByCreatedAtDesc(payment.id)
                .stream()
                .map(transactionMapper::toResponse)
                .toList();

        List<RefundResponse> refunds = refundRepository.findByPaymentId(payment.id)
                .stream()
                .map(refundMapper::toResponse)
                .toList();

        return new PaymentHistoryResponse(paymentMapper.toResponse(payment), transactions, refunds);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.status == PaymentStatus.PENDING || payment.status == PaymentStatus.FAILED) {
            throw new PaymentFailedException("Invoice is only available once payment has been completed.");
        }

        return new InvoiceResponse(
                payment.id,
                payment.booking.id,
                payment.booking.pnrCode,
                payment.transactionRef,
                payment.amount,
                payment.refundedAmount,
                payment.status,
                payment.paidAt,
                LocalDateTime.now()
        );
    }

    private Payment findByRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionRef", transactionRef));
    }

    private Transaction findTransaction(String transactionRef) {
        return transactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionRef", transactionRef));
    }

    private void log(Payment payment, String action, String details) {
        PaymentLog paymentLog = new PaymentLog();
        paymentLog.payment = payment;
        paymentLog.action = action;
        paymentLog.details = details;
        paymentLog.createdAt = LocalDateTime.now();
        paymentLogRepository.save(paymentLog);
    }
}
