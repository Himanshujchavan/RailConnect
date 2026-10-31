package com.railconnect.payment.service;

import com.railconnect.common.enums.PaymentStatus;
import com.railconnect.common.enums.RefundStatus;
import com.railconnect.common.exception.RefundNotAllowedException;
import com.railconnect.entity.Booking;
import com.railconnect.entity.Payment;
import com.railconnect.entity.Refund;
import com.railconnect.entity.Transaction;
import com.railconnect.entity.User;
import com.railconnect.payment.dtorequestresponse.RefundRequest;
import com.railconnect.payment.dtorequestresponse.RefundResponse;
import com.railconnect.payment.mapper.RefundMapper;
import com.railconnect.payment.repository.PaymentLogRepository;
import com.railconnect.payment.repository.PaymentRepository;
import com.railconnect.payment.repository.RefundRepository;
import com.railconnect.payment.repository.TransactionRepository;
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
class RefundServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RefundRepository refundRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private PaymentLogRepository paymentLogRepository;
    @Mock
    private RefundMapper refundMapper;
    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private RefundServiceImpl refundService;

    private User testUser;
    private Booking testBooking;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.id = 5L;

        testBooking = new Booking();
        testBooking.setId(50L);
        testBooking.setUser(testUser);

        testPayment = new Payment();
        testPayment.id = 500L;
        testPayment.booking = testBooking;
        testPayment.amount = 1200.0;
        testPayment.refundedAmount = 0.0;
        testPayment.status = PaymentStatus.PAID;
    }

    @Test
    @DisplayName("Should successfully initiate full refund and mark payment as REFUNDED")
    void testInitiateFullRefundSuccess() {
        RefundRequest request = new RefundRequest(500L, 1200.0, "Train cancelled");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(testPayment));
        when(refundRepository.save(any(Refund.class))).thenAnswer(i -> {
            Refund r = i.getArgument(0);
            r.id = 1L;
            return r;
        });

        RefundResponse mockResponse = new RefundResponse(
                1L, 500L, 1200.0, RefundStatus.PROCESSED, "Train cancelled", LocalDateTime.now());
        when(refundMapper.toResponse(any(Refund.class))).thenReturn(mockResponse);

        RefundResponse response = refundService.initiateRefund(request);

        assertNotNull(response);
        assertEquals(1200.0, response.amount());
        assertEquals(PaymentStatus.REFUNDED, testPayment.status);
        assertEquals(1200.0, testPayment.refundedAmount);

        verify(securityUtil).verifyUserOwnershipOrAdmin(eq(5L), anyString());
        verify(paymentRepository).save(testPayment);
        verify(refundRepository).save(any(Refund.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should successfully initiate partial refund and mark payment as PARTIALLY_REFUNDED")
    void testInitiatePartialRefundSuccess() {
        RefundRequest request = new RefundRequest(500L, 400.0, "Passenger cancelled partially");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(testPayment));
        when(refundRepository.save(any(Refund.class))).thenAnswer(i -> i.getArgument(0));

        RefundResponse mockResponse = new RefundResponse(
                2L, 500L, 400.0, RefundStatus.PROCESSED, "Partial", LocalDateTime.now());
        when(refundMapper.toResponse(any(Refund.class))).thenReturn(mockResponse);

        RefundResponse response = refundService.initiateRefund(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.PARTIALLY_REFUNDED, testPayment.status);
        assertEquals(400.0, testPayment.refundedAmount);
    }

    @Test
    @DisplayName("Should throw RefundNotAllowedException when requested refund exceeds refundable balance")
    void testInitiateRefundExceedsBalance() {
        testPayment.refundedAmount = 1000.0; // remaining balance is 200
        RefundRequest request = new RefundRequest(500L, 300.0, "Too much");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(testPayment));

        assertThrows(RefundNotAllowedException.class, () -> refundService.initiateRefund(request));
        verify(refundRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RefundNotAllowedException when payment is not in PAID or PARTIALLY_REFUNDED status")
    void testInitiateRefundUnpaid() {
        testPayment.status = PaymentStatus.PENDING;
        RefundRequest request = new RefundRequest(500L, 500.0, "Not yet paid");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(testPayment));

        assertThrows(RefundNotAllowedException.class, () -> refundService.initiateRefund(request));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when ownership verification fails")
    void testInitiateRefundAccessDenied() {
        RefundRequest request = new RefundRequest(500L, 500.0, "Unauthorized request");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(testPayment));
        doThrow(new AccessDeniedException("Access denied")).when(securityUtil)
                .verifyUserOwnershipOrAdmin(eq(5L), anyString());

        assertThrows(AccessDeniedException.class, () -> refundService.initiateRefund(request));
        verify(paymentRepository, never()).save(any());
    }
}
