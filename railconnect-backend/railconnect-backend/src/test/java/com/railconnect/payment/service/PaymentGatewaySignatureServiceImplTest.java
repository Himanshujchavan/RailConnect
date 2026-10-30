package com.railconnect.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentGatewaySignatureServiceImplTest {

    private PaymentGatewaySignatureServiceImpl signatureService;

    @BeforeEach
    void setUp() {
        signatureService = new PaymentGatewaySignatureServiceImpl("test-secret-key-32bytes-long-12345");
    }

    @Test
    @DisplayName("Should generate deterministic HMAC-SHA256 signature and successfully verify it")
    void testGenerateAndVerifySignatureSuccess() {
        String txRef = "TXN-TEST-123456";
        Double amount = 750.50;
        Long bookingId = 42L;

        String signature = signatureService.generateSignature(txRef, amount, bookingId);

        assertNotNull(signature);
        assertFalse(signature.isBlank());
        assertTrue(signatureService.verifySignature(txRef, amount, bookingId, signature));
    }

    @Test
    @DisplayName("Should reject tampered transactionRef")
    void testRejectTamperedTransactionRef() {
        String signature = signatureService.generateSignature("TXN-1", 500.0, 1L);
        assertFalse(signatureService.verifySignature("TXN-2", 500.0, 1L, signature));
    }

    @Test
    @DisplayName("Should reject tampered amount")
    void testRejectTamperedAmount() {
        String signature = signatureService.generateSignature("TXN-1", 500.0, 1L);
        assertFalse(signatureService.verifySignature("TXN-1", 100.0, 1L, signature));
    }

    @Test
    @DisplayName("Should reject tampered bookingId")
    void testRejectTamperedBookingId() {
        String signature = signatureService.generateSignature("TXN-1", 500.0, 1L);
        assertFalse(signatureService.verifySignature("TXN-1", 500.0, 99L, signature));
    }

    @Test
    @DisplayName("Should return false for null or blank signature")
    void testNullOrBlankSignature() {
        assertFalse(signatureService.verifySignature("TXN-1", 500.0, 1L, null));
        assertFalse(signatureService.verifySignature("TXN-1", 500.0, 1L, ""));
        assertFalse(signatureService.verifySignature("TXN-1", 500.0, 1L, "   "));
    }
}
