package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.Notification;
import com.opsmonsters.quick_bite.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserId(Long userId);
    List<Notification> findByUser(Users user);
}





