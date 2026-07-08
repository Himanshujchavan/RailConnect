package com.railconnect.notification;

import com.railconnect.entity.Notification;
import com.railconnect.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email based notification provider using Spring's JavaMailSender.
 */
@Component
public class EmailNotificationProvider implements NotificationProvider {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final String fromAddress;

    @Autowired
    public EmailNotificationProvider(JavaMailSender mailSender,
                                    NotificationRepository notificationRepository,
                                    @Value("${railconnect.notification.email.from:no-reply@railconnect.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendInApp(Long userId, String title, String body) {
        // Not handled here – delegate to InApp provider.
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // Persist an in‑app notification as well for record‑keeping.
        Notification notification = new Notification();
        // Assuming Notification has a setter for user – for now we keep user null (could be system).
        notification.setTitle(subject);
        notification.setMessage(body);
        notification.setSentAt(java.time.LocalDateTime.now());
        notificationRepository.save(notification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
