package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.NotificationDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.models.Notification;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.repositories.NotificationRepo;
import com.opsmonsters.quick_bite.repositories.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;

    public NotificationService(NotificationRepo notificationRepo, UserRepo userRepo) {
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
    }


    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepo.findByUser_UserId(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public ResponseDto createNotification(NotificationDto notificationDto) {
        Users user = userRepo.findById(notificationDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        Notification notification = new Notification(
                notificationDto.getMessage(),
                notificationDto.getType(),
                user
        );

        notificationRepo.save(notification);
        return new ResponseDto(201, "Notification created successfully!", convertToDto(notification));
    }


    @Transactional
    public ResponseDto markAsRead(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found!"));

        notification.setRead(true);
        notificationRepo.save(notification);

        return new ResponseDto(200, "Notification marked as read!", convertToDto(notification));
    }


    @Transactional
    public ResponseDto deleteNotification(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found!"));

        notificationRepo.delete(notification);
        return new ResponseDto(200, "Notification deleted successfully!", null);
    }


    private NotificationDto convertToDto(Notification notification) {
        return new NotificationDto(
                notification.getNotificationId(),
                notification.getUser().getUserId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}