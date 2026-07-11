package com.railconnect.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock email notification provider that simply logs the email payload.
 * This satisfies the architecture decoupling requirement and can be swapped
 * for a real provider (SendGrid, AWS SES, etc.) in a later phase.
 */
@Slf4j
@Component
public class EmailNotificationProviderImpl implements NotificationProvider {

    @Override
    public void sendInApp(Long userId, String title, String body) {
        // Not applicable for email provider – no‑op.
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("[Mock Email] To: {}, Subject: {}, Body: {}", to, subject, body);
    }

    @Override
    public void sendSms(String to, String message) {
        // Not applicable for this mock email provider – no‑op.
    }

    @Override
    public void sendPush(Long userId, String title, String body) {
        // Not applicable for this mock email provider – no‑op.
    }
}
