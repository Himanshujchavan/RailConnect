package com.railconnect.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mock push notification provider that logs the payload instead of calling a real push gateway
 * (FCM, APNs, etc). Swappable for a real implementation later without touching call sites.
 * Delivery only - persistence of the Notification record is centralized in NotificationServiceImpl.
 */
@Component
public class PushNotificationProvider implements NotificationProvider {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationProvider.class);

    @Override
    public void sendInApp(Long userId, String title, String body) {
        // Not applicable for this provider – no‑op.
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Not applicable for this provider – no‑op.
    }

    @Override
    public void sendSms(String to, String message) {
        // Not applicable for this provider – no‑op.
    }

    @Override
    public void sendPush(Long userId, String title, String body) {
        logger.info("[Mock Push] UserId: {}, Title: {}, Body: {}", userId, title, body);
    }
}
