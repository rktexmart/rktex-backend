package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.NotificationDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})
@RequestMapping("/auth/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }


    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createNotification(@RequestBody NotificationDto notificationDto) {
        if (notificationDto.getUserId() == null || notificationDto.getMessage() == null || notificationDto.getType() == null) {
            return ResponseEntity.badRequest().body(new ResponseDto(400, "User ID, message, and type are required!", null));
        }
        ResponseDto response = notificationService.createNotification(notificationDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ResponseDto> markNotificationAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ResponseDto> deleteNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }
}





