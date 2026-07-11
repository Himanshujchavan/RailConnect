package com.railconnect.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email based notification provider using Spring's JavaMailSender.
 * Delivery only - persistence of the Notification record is centralized in NotificationServiceImpl.
 */
@Component
public class EmailNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    @Autowired
    public EmailNotificationProvider(JavaMailSender mailSender,
                                    @Value("${railconnect.notification.email.from:no-reply@railconnect.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendInApp(Long userId, String title, String body) {
        // Not handled here – delegate to InApp provider.
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException ex) {
            throw new NotificationDeliveryException("Failed to send email to " + to, ex);
        }
    }

    @Override
    public void sendSms(String to, String message) {
        // Email provider does not handle SMS. No‑op – handled by SmsNotificationProvider.
    }

    @Override
    public void sendPush(Long userId, String title, String body) {
        // Email provider does not handle push. No‑op – handled by PushNotificationProvider.
    }
}
