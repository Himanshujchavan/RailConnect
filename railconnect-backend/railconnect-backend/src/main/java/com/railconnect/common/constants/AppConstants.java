package com.railconnect.common.constants;

public final class AppConstants {
    
    private AppConstants() {
        // Prevent instantiation
    }

    // Pagination defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    // Business Rules
    public static final int MAX_PASSENGERS_PER_BOOKING = 6;
    public static final int TICKET_LOCK_TIMEOUT_MINUTES = 10;
    public static final double BASE_CANCELLATION_CHARGE = 60.0;
}