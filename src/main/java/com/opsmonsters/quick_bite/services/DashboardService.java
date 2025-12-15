package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.*;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final UserRepo userRepo;
    private final AddressRepo addressRepo;
    private final OrderRepo orderRepo;
    private final PromoCodeRepo promoCodeRepo;
    private final NotificationRepo notificationRepo;

    public DashboardService(UserRepo userRepo, AddressRepo addressRepo, OrderRepo orderRepo,
                            PromoCodeRepo promoCodeRepo, NotificationRepo notificationRepo) {
        this.userRepo = userRepo;
        this.addressRepo = addressRepo;
        this.orderRepo = orderRepo;
        this.promoCodeRepo = promoCodeRepo;
        this.notificationRepo = notificationRepo;
    }

    public DashboardDto getUserDashboard(Long userId) {
        logger.info("Fetching dashboard for userId: {}", userId);

        Users user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            logger.warn("User not found with ID: {}", userId);
            return new DashboardDto(
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    "User not found."
            );
        }

        logger.info("User found: {}", user.getEmail());

        List<AddressDto> addresses = addressRepo.findByUser(user).stream()
                .map(AddressDto::new)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new LinkedHashSet<>()),
                        List::copyOf)
                );

        List<OrderDto> orders = orderRepo.findByUser(user).stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        List<PromoCodeDto> promoCodes = promoCodeRepo.findAll().stream()
                .map(PromoCodeDto::new)
                .collect(Collectors.toList());

        List<NotificationDto> notifications = notificationRepo.findByUser(user).stream()
                .map(NotificationDto::new)
                .collect(Collectors.toList());

        return new DashboardDto(addresses, orders, promoCodes, notifications, "Logout successfully.");
    }

    public DashboardDto logoutUser() {
        SecurityContextHolder.clearContext();
        logger.info("User has been logged out.");
        return new DashboardDto(List.of(), List.of(), List.of(), List.of(), "Logout successful.");
    }
}