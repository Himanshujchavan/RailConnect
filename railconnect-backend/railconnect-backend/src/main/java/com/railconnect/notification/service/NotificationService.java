package com.railconnect.notification.service;

import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.notification.dtorequestresponse.NotificationSendRequest;

import java.util.List;

public interface NotificationService {

    /**
     * Dispatches a notification across every requested channel. Each channel is attempted
     * independently - one failing (e.g. SMTP down) does not stop the others - and a
     * Notification record is persisted per channel reflecting the outcome.
     */
    List<NotificationResponse> send(NotificationSendRequest request);

    List<NotificationResponse> getHistory(Long userId);

    NotificationResponse markAsRead(Long notificationId);
}
