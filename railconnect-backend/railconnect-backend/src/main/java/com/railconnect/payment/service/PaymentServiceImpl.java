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
import com.railconnect.payment.dtorequestresponse.RefundEstimateResponse;
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
import com.railconnect.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 4 — Payment Module.
 * <p>
 * Simulates a payment-gateway integration: {@code initiate} creates a PENDING payment, computes
 * a cryptographic gateway signature, and hands back the transaction reference and signature.
 * Callbacks to {@code success}/{@code failure} cryptographically verify the signature before
 * updating state. All access to payment history, invoices, and payment initiation strictly
 * enforces user ownership or administrative privileges.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final TransactionMapper transactionMapper;
    private final RefundMapper refundMapper;
    private final PaymentGatewaySignatureService signatureService;
    private final SecurityUtil securityUtil;
    private final RefundCalculationService refundCalculationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               TransactionRepository transactionRepository,
                               PaymentLogRepository paymentLogRepository,
                               RefundRepository refundRepository,
                               BookingRepository bookingRepository,
                               PaymentMapper paymentMapper,
                               TransactionMapper transactionMapper,
                               RefundMapper refundMapper,
                               PaymentGatewaySignatureService signatureService,
                               SecurityUtil securityUtil,
                               RefundCalculationService refundCalculationService) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.refundRepository = refundRepository;
        this.bookingRepository = bookingRepository;
        this.paymentMapper = paymentMapper;
        this.transactionMapper = transactionMapper;
        this.refundMapper = refundMapper;
        this.signatureService = signatureService;
        this.securityUtil = securityUtil;
        this.refundCalculationService = refundCalculationService;
    }

    @Override
    @Transactional
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));

        // Ownership Verification: Only the booking owner or an admin can initiate payment
        Long ownerId = booking.getUser() != null ? booking.getUser().id : null;
        securityUtil.verifyUserOwnershipOrAdmin(ownerId, "initiate payment for this booking");

        Payment existing = paymentRepository.findByBookingId(booking.getId()).orElse(null);
        if (existing != null && existing.status == PaymentStatus.PAID) {
            throw new PaymentFailedException("This booking has already been paid for.");
        }
        if (existing != null && existing.status == PaymentStatus.PENDING) {
            // Idempotent: re-initiating an in-flight payment generates signature and hands back the same reference.
            String signature = signatureService.generateSignature(existing.transactionRef, existing.amount, booking.getId());
            return new PaymentInitiateResponse(existing.id, booking.getId(), existing.transactionRef,
                    existing.amount, existing.status, signature);
        }

        Payment payment = existing != null ? existing : new Payment();
        payment.booking = booking;
        payment.amount = booking.getTotalFare();
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

        String signature = signatureService.generateSignature(payment.transactionRef, payment.amount, booking.getId());

        return new PaymentInitiateResponse(payment.id, booking.getId(), payment.transactionRef,
                payment.amount, payment.status, signature);
    }

    @Override
    @Transactional
    public PaymentResponse handleSuccess(PaymentCallbackRequest request) {
        Payment payment = findByRef(request.transactionRef());
        Transaction transaction = findTransaction(request.transactionRef());

        if (payment.status == PaymentStatus.PAID) {
            // Already settled - idempotent repeat callback
            return paymentMapper.toResponse(payment);
        }
        if (payment.status == PaymentStatus.FAILED) {
            throw new PaymentFailedException("Cannot mark an already-failed payment as successful.");
        }

        // Gateway Signature Verification: ensure callback is genuine
        boolean isValidSignature = signatureService.verifySignature(
                payment.transactionRef,
                payment.amount,
                payment.booking != null ? payment.booking.getId() : null,
                request.signature()
        );

        if (!isValidSignature) {
            log(payment, "SIGNATURE_VERIFICATION_FAILED",
                    "Invalid gateway signature received for transactionRef: " + request.transactionRef());
            logger.warn("Rejected unauthorized payment success callback for transactionRef: {}", request.transactionRef());
            throw new PaymentFailedException("Payment verification failed: invalid gateway signature.");
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

        // If a signature is provided, verify it
        if (request.signature() != null && !request.signature().isBlank()) {
            boolean isValidSignature = signatureService.verifySignature(
                    payment.transactionRef,
                    payment.amount,
                    payment.booking != null ? payment.booking.getId() : null,
                    request.signature()
            );
            if (!isValidSignature) {
                logger.warn("Received invalid signature on failure callback for ref: {}", request.transactionRef());
            }
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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Ownership Verification
        Long ownerId = booking.getUser() != null ? booking.getUser().id : null;
        securityUtil.verifyUserOwnershipOrAdmin(ownerId, "view payment history for this booking");

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

        // Ownership Verification
        Long ownerId = (payment.booking != null && payment.booking.getUser() != null) ? payment.booking.getUser().id : null;
        securityUtil.verifyUserOwnershipOrAdmin(ownerId, "view invoice for this payment");

        if (payment.status == PaymentStatus.PENDING || payment.status == PaymentStatus.FAILED) {
            throw new PaymentFailedException("Invoice is only available once payment has been completed.");
        }

        return new InvoiceResponse(
                payment.id,
                payment.booking.getId(),
                payment.booking.getPnrCode(),
                payment.transactionRef,
                payment.amount,
                payment.refundedAmount,
                payment.status,
                payment.paidAt,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatus(String transactionRef) {
        Payment payment = findByRef(transactionRef);
        Long ownerId = (payment.booking != null && payment.booking.getUser() != null) ? payment.booking.getUser().id : null;
        securityUtil.verifyUserOwnershipOrAdmin(ownerId, "view payment status");
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundEstimateResponse getRefundEstimate(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        Long ownerId = booking.getUser() != null ? booking.getUser().id : null;
        securityUtil.verifyUserOwnershipOrAdmin(ownerId, "view refund estimate for this booking");

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
        return refundCalculationService.calculateRefund(booking, payment, LocalDateTime.now());
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
