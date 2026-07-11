package com.railconnect.notification;

/**
 * Default title/body copy for each {@link NotificationType}, used whenever a caller doesn't
 * supply an explicit override. {@code details} is free-text context spliced into the body -
 * e.g. a PNR, a new berth/quota, or a delay duration.
 */
public final class NotificationTemplates {

    private NotificationTemplates() {
        // Prevent instantiation
    }

    public static String title(NotificationType type) {
        return switch (type) {
            case BOOKING_CONFIRMED -> "Booking Confirmed";
            case BOOKING_CANCELLED -> "Booking Cancelled";
            case RAC_UPGRADED -> "RAC Upgraded";
            case WL_PROMOTED -> "Waiting List Confirmed";
            case TRAIN_DELAYED -> "Train Delayed";
        };
    }

    public static String body(NotificationType type, String recipientName, String details) {
        String name = (recipientName == null || recipientName.isBlank()) ? "Passenger" : recipientName;
        String extra = (details == null || details.isBlank()) ? "" : " " + details;

        return switch (type) {
            case BOOKING_CONFIRMED -> "Hi " + name + ", your booking is confirmed." + extra;
            case BOOKING_CANCELLED -> "Hi " + name + ", your booking has been cancelled." + extra;
            case RAC_UPGRADED -> "Hi " + name + ", your ticket has been upgraded from waiting list to RAC." + extra;
            case WL_PROMOTED -> "Hi " + name + ", your waiting list ticket has been confirmed." + extra;
            case TRAIN_DELAYED -> "Hi " + name + ", your train is running late." + extra;
        };
    }
}
