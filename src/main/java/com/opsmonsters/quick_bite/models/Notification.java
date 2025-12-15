package com.opsmonsters.quick_bite.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    private String message;
    private String type;
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    public Notification() {}

    public Notification(String message, String type, Users user) {
        this.message = message;
        this.type = type;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }


    public Long getNotificationId() { return notificationId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Users getUser() { return user; }

    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
    public void setMessage(String message) { this.message = message; }
    public void setType(String type) { this.type = type; }
    public void setRead(boolean read) { isRead = read; }
    public void setUser(Users user) { this.user = user; }
}