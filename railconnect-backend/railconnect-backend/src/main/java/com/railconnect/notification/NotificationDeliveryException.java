package com.railconnect.notification;

/**
 * Thrown by a {@link NotificationProvider} when it fails to deliver a message through its
 * channel (SMTP error, gateway timeout, etc). Callers catch this per-channel so one failing
 * channel doesn't stop the others from being attempted.
 */
public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationDeliveryException(String message) {
        super(message);
    }
}
