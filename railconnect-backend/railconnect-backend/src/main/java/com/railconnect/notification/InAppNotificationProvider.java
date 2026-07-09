package com.railconnect.notification;

import com.railconnect.entity.Notification;
import com.railconnect.entity.User;
import com.railconnect.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Simple in‑app notification implementation that persists a Notification entity.
 */
@Component
public class InAppNotificationProvider implements NotificationProvider {

    private final NotificationRepository notificationRepository;

    @Autowired
    public InAppNotificationProvider(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendInApp(Long userId, String title, String body) {
        Notification notification = new Notification();
        notification.setUser(User.builder().id(userId).build());
        notification.setTitle(title);
        notification.setMessage(body);
        notification.setSentAt(java.time.LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        // In‑app provider does not handle email.
        // No‑op – Email will be handled by EmailNotificationProvider.
    }
}
