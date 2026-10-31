package com.railconnect.payment.service;

import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.common.enums.TransactionStatus;
import com.railconnect.common.exception.PaymentFailedException;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.entity.Transaction;
import com.railconnect.entity.User;
import com.railconnect.payment.dtorequestresponse.PaymentCallbackRequest;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateRequest;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateResponse;
import com.railconnect.payment.dtorequestresponse.PaymentResponse;
import com.railconnect.payment.mapper.PaymentMapper;
import com.railconnect.payment.mapper.RefundMapper;
import com.railconnect.payment.mapper.TransactionMapper;
import com.railconnect.payment.repository.PaymentLogRepository;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.payment.repository.RefundRepository;
import com.railconnect.payment.repository.TransactionRepository;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private PaymentLogRepository paymentLogRepository;
    @Mock
    private RefundRepository refundRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private RefundMapper refundMapper;
    @Mock
    private PaymentGatewaySignatureService signatureService;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private RefundCalculationService refundCalculationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User testUser;
    private Booking testBooking;
    private Payment testPayment;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.id = 1L;
        testUser.username = "testuser";

        testBooking = new Booking();
        testBooking.setId(10L);
        testBooking.setUser(testUser);
        testBooking.setTotalFare(850.0);

        testPayment = new Payment();
        testPayment.id = 100L;
        testPayment.booking = testBooking;
        testPayment.amount = 850.0;
        testPayment.transactionRef = "TXN-TEST-100";
        testPayment.status = PaymentStatus.PENDING;
        testPayment.refundedAmount = 0.0;

        testTransaction = new Transaction();
        testTransaction.id = 1000L;
        testTransaction.transactionRef = "TXN-TEST-100";
        testTransaction.status = TransactionStatus.PENDING;
    }

    @Test
    @DisplayName("Should initiate payment and return valid gateway signature")
    void testInitiatePaymentSuccess() {
        PaymentInitiateRequest request = new PaymentInitiateRequest(10L, "UPI");

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(testBooking));
        when(paymentRepository.findByBookingId(10L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.id = 100L;
            return p;
        });
        when(signatureService.generateSignature(any(), any(), eq(10L))).thenReturn("mock-valid-sig-123");

        PaymentInitiateResponse response = paymentService.initiatePayment(request);

        assertNotNull(response);
        assertEquals(10L, response.bookingId());
        assertEquals(850.0, response.amount());
        assertEquals("mock-valid-sig-123", response.gatewaySignature());
        assertEquals(PaymentStatus.PENDING, response.status());

        verify(securityUtil).verifyUserOwnershipOrAdmin(eq(1L), anyString());
        verify(paymentRepository).save(any(Payment.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when ownership check fails on payment initiation")
    void testInitiatePaymentAccessDenied() {
        PaymentInitiateRequest request = new PaymentInitiateRequest(10L, "UPI");

        when(bookingRepository.findById(10L)).thenReturn(Optional.of(testBooking));
        doThrow(new AccessDeniedException("Access denied")).when(securityUtil)
                .verifyUserOwnershipOrAdmin(eq(1L), anyString());

        assertThrows(AccessDeniedException.class, () -> paymentService.initiatePayment(request));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully handle success callback with valid signature")
    void testHandleSuccessValidSignature() {
        PaymentCallbackRequest request = new PaymentCallbackRequest("TXN-TEST-100", "GATEWAY-AUTH-OK", "valid-sig");

        when(paymentRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testPayment));
        when(transactionRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testTransaction));
        when(signatureService.verifySignature(eq("TXN-TEST-100"), eq(850.0), eq(10L), eq("valid-sig"))).thenReturn(true);

        PaymentResponse mockResponse = new PaymentResponse(
                100L, 850.0, 0.0, LocalDateTime.now(), PaymentStatus.PAID, "TXN-TEST-100", "UPI", 10L);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(mockResponse);

        PaymentResponse response = paymentService.handleSuccess(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.PAID, response.status());
        assertEquals(PaymentStatus.PAID, testPayment.status);
        assertEquals(TransactionStatus.SUCCESS, testTransaction.status);
        verify(paymentRepository).save(testPayment);
    }

    @Test
    @DisplayName("Should reject success callback with invalid signature and throw PaymentFailedException")
    void testHandleSuccessInvalidSignature() {
        PaymentCallbackRequest request = new PaymentCallbackRequest("TXN-TEST-100", "FORGED-GATEWAY-REF", "fake-sig");

        when(paymentRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testPayment));
        when(transactionRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testTransaction));
        when(signatureService.verifySignature(eq("TXN-TEST-100"), eq(850.0), eq(10L), eq("fake-sig"))).thenReturn(false);

        PaymentFailedException ex = assertThrows(PaymentFailedException.class, () -> paymentService.handleSuccess(request));
        assertTrue(ex.getMessage().contains("invalid gateway signature"));
        assertEquals(PaymentStatus.PENDING, testPayment.status);
    }

    @Test
    @DisplayName("Should be idempotent if payment is already marked as PAID")
    void testHandleSuccessIdempotent() {
        testPayment.status = PaymentStatus.PAID;
        PaymentCallbackRequest request = new PaymentCallbackRequest("TXN-TEST-100", "ANY-REF", "any-sig");

        when(paymentRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testPayment));
        when(transactionRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testTransaction));

        PaymentResponse mockResponse = new PaymentResponse(
                100L, 850.0, 0.0, LocalDateTime.now(), PaymentStatus.PAID, "TXN-TEST-100", "UPI", 10L);
        when(paymentMapper.toResponse(testPayment)).thenReturn(mockResponse);

        PaymentResponse response = paymentService.handleSuccess(request);

        assertEquals(PaymentStatus.PAID, response.status());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle failure callback and mark payment as FAILED")
    void testHandleFailure() {
        PaymentCallbackRequest request = new PaymentCallbackRequest("TXN-TEST-100", "INSUFFICIENT_FUNDS", null);

        when(paymentRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testPayment));
        when(transactionRepository.findByTransactionRef("TXN-TEST-100")).thenReturn(Optional.of(testTransaction));

        PaymentResponse mockResponse = new PaymentResponse(
                100L, 850.0, 0.0, null, PaymentStatus.FAILED, "TXN-TEST-100", "UPI", 10L);
        when(paymentMapper.toResponse(testPayment)).thenReturn(mockResponse);

        PaymentResponse response = paymentService.handleFailure(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, testPayment.status);
        assertEquals(TransactionStatus.FAILED, testTransaction.status);
    }
}
