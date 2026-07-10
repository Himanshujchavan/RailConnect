package com.railconnect.common.util;

import java.util.UUID;

public final class TransactionRefGenerator {

    private TransactionRefGenerator() {
        // Prevent instantiation
    }

    /**
     * Generates a unique, gateway-style transaction reference, e.g. TXN-4F2A9C7E1B3D4A5E.
     */
    public static String generate() {
        String raw = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        return "TXN-" + raw;
    }
}
