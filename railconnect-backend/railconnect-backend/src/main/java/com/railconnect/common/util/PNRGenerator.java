package com.railconnect.common.util;

import java.util.concurrent.ThreadLocalRandom;

public final class PNRGenerator {

    private PNRGenerator() {
        // Prevent instantiation
    }

    /**
     * Generates a unique 10-digit numeric PNR string.
     */
    public static String generatePNR() {
        // Generates a random number between 1,000,000,000 and 9,999,999,999
        long pnrNumber = ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
        return String.valueOf(pnrNumber);
    }
}