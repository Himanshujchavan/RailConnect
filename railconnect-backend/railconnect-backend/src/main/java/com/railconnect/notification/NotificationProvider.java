package com.railconnect.notification;

/**
 * Abstraction for sending notifications.
 * Implementations can provide in‑app, email, SMS, push, etc.
 */
public interface NotificationProvider {

    /**
     * Send an in‑app notification to the given user.
     */
    void sendInApp(Long userId, String title, String body);

    /**
     * Send an email notification.
     * @param to recipient email address
     * @param subject email subject
     * @param body email body (plain text)
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Send an SMS notification.
     * @param to recipient phone number
     * @param message SMS body (kept short by convention, but not enforced here)
     */
    void sendSms(String to, String message);

    /**
     * Send a push notification.
     * @param userId recipient's user id (device/token lookup is an implementation detail)
     * @param title push notification title
     * @param body push notification body
     */
    void sendPush(Long userId, String title, String body);
}
