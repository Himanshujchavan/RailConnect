package com.railconnect.notification.dtorequestresponse;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String notificationType; // e.g., "BOOKING_CONFIRMED", "TRAIN_DELAYED"
    private String channel;          // e.g., "EMAIL", "SMS", "PUSH", "IN_APP"
    private String status;           // e.g., "SENT", "FAILED"
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponse() {}

    public NotificationResponse(Long id, Long userId, String title, String message, String notificationType,
                                 String channel, String status, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.channel = channel;
        this.status = status;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
