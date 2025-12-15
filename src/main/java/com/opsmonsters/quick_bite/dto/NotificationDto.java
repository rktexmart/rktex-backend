package com.opsmonsters.quick_bite.dto;

import com.opsmonsters.quick_bite.models.Notification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationDto {
    private Long notificationId;
    private Long userId;
    private String message;
    private String type;
    private boolean isRead;
    private String formattedCreatedAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public NotificationDto() {
    }

    public NotificationDto(Long notificationId, Long userId, String message, String type, boolean isRead, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.formattedCreatedAt = createdAt != null ? createdAt.format(formatter) : null;
    }

    public NotificationDto(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.userId = notification.getUser() != null ? notification.getUser().getUserId() : null;
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.isRead = notification.isRead();
        this.formattedCreatedAt = notification.getCreatedAt() != null ? notification.getCreatedAt().format(formatter) : null;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }

    public void setFormattedCreatedAt(LocalDateTime createdAt) {
        this.formattedCreatedAt = createdAt != null ? createdAt.format(formatter) : null;
    }
}