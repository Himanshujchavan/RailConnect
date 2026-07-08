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
}
