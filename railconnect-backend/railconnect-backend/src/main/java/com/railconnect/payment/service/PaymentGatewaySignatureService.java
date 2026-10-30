package com.railconnect.payment.service;

/**
 * Service responsible for generating and validating cryptographic HMAC-SHA256 signatures
 * for payment transactions, preventing unauthorized or forged callback payloads.
 */
public interface PaymentGatewaySignatureService {

    /**
     * Generates an HMAC-SHA256 hex signature for a given transaction reference, amount, and booking ID.
     */
    String generateSignature(String transactionRef, Double amount, Long bookingId);

    /**
     * Verifies whether the provided signature matches the expected HMAC-SHA256 signature.
     */
    boolean verifySignature(String transactionRef, Double amount, Long bookingId, String providedSignature);
}
