package com.railconnect.common.constants;

public final class MessageConstants {

    private MessageConstants() {
        // Prevent instantiation
    }

    // Success Messages
    public static final String BOOKING_SUCCESS = "Ticket booked successfully.";
    public static final String PAYMENT_SUCCESS = "Payment processed successfully.";
    public static final String PASSWORD_RESET_SUCCESS = "Your password has been successfully reset.";

    // Error & Validation Fallbacks
    public static final String RESOURCE_NOT_FOUND = "The requested resource could not be found.";
    public static final String INVALID_CREDENTIALS = "Invalid username or password credentials.";
    public static final String SEAT_UNAVAILABLE = "Selected seats are no longer available.";
    public static final String TRANSACTION_FAILED = "Payment gateway transaction dropped or failed.";
}
