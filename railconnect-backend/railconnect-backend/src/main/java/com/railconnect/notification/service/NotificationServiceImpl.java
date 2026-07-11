package com.railconnect.notification.service;

import com.railconnect.auth.repository.UserRepository;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.entity.Notification;
import com.railconnect.entity.User;
import com.railconnect.notification.NotificationChannel;
import com.railconnect.notification.NotificationDeliveryException;
import com.railconnect.notification.NotificationProvider;
import com.railconnect.notification.NotificationStatus;
import com.railconnect.notification.NotificationTemplates;
import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;
import com.railconnect.notification.mapper.NotificationMapper;
import com.railconnect.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 5 — Notification Module.
 * <p>
 * Persistence lives here rather than in the individual providers: for every channel requested,
 * this class attempts delivery through the matching {@link NotificationProvider}, then writes
 * one {@link Notification} row reflecting the outcome (SENT or FAILED). A failure on one
 * channel never stops the others from being attempted.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationProvider emailProvider;
    private final NotificationProvider smsProvider;
    private final NotificationProvider pushProvider;
    private final NotificationProvider inAppProvider;

    public NotificationServiceImpl(UserRepository userRepository,
                                    NotificationRepository notificationRepository,
                                    NotificationMapper notificationMapper,
                                    @Qualifier("emailNotificationProvider") NotificationProvider emailProvider,
                                    @Qualifier("smsNotificationProvider") NotificationProvider smsProvider,
                                    @Qualifier("pushNotificationProvider") NotificationProvider pushProvider,
                                    @Qualifier("inAppNotificationProvider") NotificationProvider inAppProvider) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.emailProvider = emailProvider;
        this.smsProvider = smsProvider;
        this.pushProvider = pushProvider;
        this.inAppProvider = inAppProvider;
    }

    @Override
    @Transactional
    public List<NotificationResponse> send(NotificationSendRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        String title = request.titleOverride() != null
                ? request.titleOverride()
                : NotificationTemplates.title(request.type());
        String body = request.messageOverride() != null
                ? request.messageOverride()
                : NotificationTemplates.body(request.type(), user.fullName(), request.details());

        List<NotificationResponse> responses = new ArrayList<>();
        for (NotificationChannel channel : request.channels()) {
            NotificationStatus status = dispatch(channel, user, title, body);

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setMessage(body);
            notification.setType(request.type());
            notification.setChannel(channel);
            notification.setStatus(status);
            notification.setSentAt(LocalDateTime.now());
            notification.setRead(false);

            Notification saved = notificationRepository.save(notification);
            responses.add(notificationMapper.toResponse(saved));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getHistory(Long userId) {
        return notificationMapper.toResponseList(
                notificationRepository.findByUserIdOrderBySentAtDesc(userId));
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notification.setRead(true);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    private NotificationStatus dispatch(NotificationChannel channel, User user, String title, String body) {
        try {
            switch (channel) {
                case EMAIL -> emailProvider.sendEmail(user.email, title, body);
                case SMS -> smsProvider.sendSms(user.phone, body);
                case PUSH -> pushProvider.sendPush(user.id, title, body);
                case IN_APP -> inAppProvider.sendInApp(user.id, title, body);
            }
            return NotificationStatus.SENT;
        } catch (NotificationDeliveryException ex) {
            logger.warn("Notification delivery failed on channel {} for user {}: {}", channel, user.id, ex.getMessage());
            return NotificationStatus.FAILED;
        }
    }
}
