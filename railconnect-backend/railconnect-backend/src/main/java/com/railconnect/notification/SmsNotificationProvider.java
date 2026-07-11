package com.railconnect.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mock SMS notification provider that logs the payload instead of calling a real SMS gateway
 * (Twilio, SNS, etc). Swappable for a real implementation later without touching call sites.
 * Delivery only - persistence of the Notification record is centralized in NotificationServiceImpl.
 */
@Component
public class SmsNotificationProvider implements NotificationProvider {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationProvider.class);

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
        if (to == null || to.isBlank()) {
            throw new NotificationDeliveryException("No phone number on file for this user.");
        }
        logger.info("[Mock SMS] To: {}, Message: {}", to, message);
    }

    @Override
    public void sendPush(Long userId, String title, String body) {
        // Not applicable for this provider – no‑op.
    }
}
