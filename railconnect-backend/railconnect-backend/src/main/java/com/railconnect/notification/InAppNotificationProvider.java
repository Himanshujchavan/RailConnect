package com.railconnect.notification;

import org.springframework.stereotype.Component;

/**
 * In-app "delivery" is just the Notification row itself, which NotificationServiceImpl already
 * persists centrally for every channel - so there's nothing extra to do here. Kept as an
 * explicit no-op implementation so IN_APP behaves like any other channel from the caller's
 * point of view.
 */
@Component
public class InAppNotificationProvider implements NotificationProvider {

    @Override
    public void sendInApp(Long userId, String title, String body) {
        // No-op - the persisted Notification row (written by NotificationServiceImpl) is the notification.
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // In‑app provider does not handle email. No‑op – handled by EmailNotificationProvider.
    }

    @Override
    public void sendSms(String to, String message) {
        // In‑app provider does not handle SMS. No‑op – handled by SmsNotificationProvider.
    }

    @Override
    public void sendPush(Long userId, String title, String body) {
        // In‑app provider does not handle push. No‑op – handled by PushNotificationProvider.
    }
}
