package com.railconnect.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class PaymentGatewaySignatureServiceImpl implements PaymentGatewaySignatureService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewaySignatureServiceImpl.class);
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private final String secretKey;

    public PaymentGatewaySignatureServiceImpl(
            @Value("${railconnect.payment.gateway-secret:railconnect-secret-payment-gateway-key-32bytes}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String generateSignature(String transactionRef, Double amount, Long bookingId) {
        try {
            String payload = buildPayload(transactionRef, amount, bookingId);
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            logger.error("Failed to generate payment signature for transactionRef: {}", transactionRef, e);
            throw new IllegalStateException("Failed to generate payment gateway signature", e);
        }
    }

    @Override
    public boolean verifySignature(String transactionRef, Double amount, Long bookingId, String providedSignature) {
        if (providedSignature == null || providedSignature.isBlank()) {
            return false;
        }
        try {
            String expectedSignature = generateSignature(transactionRef, amount, bookingId);
            byte[] expectedBytes = expectedSignature.getBytes(StandardCharsets.UTF_8);
            byte[] providedBytes = providedSignature.getBytes(StandardCharsets.UTF_8);
            // Constant-time comparison to mitigate side-channel timing attacks
            return MessageDigest.isEqual(expectedBytes, providedBytes);
        } catch (Exception e) {
            logger.warn("Signature verification failed with exception for transactionRef: {}", transactionRef, e);
            return false;
        }
    }

    private String buildPayload(String transactionRef, Double amount, Long bookingId) {
        // Standardize format: reference|formatted_amount|booking_id
        String formattedAmount = amount != null ? String.format("%.2f", amount) : "0.00";
        return String.format("%s|%s|%d", transactionRef, formattedAmount, bookingId != null ? bookingId : 0L);
    }
}
