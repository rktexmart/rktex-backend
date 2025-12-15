package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.DashboardDto;
import com.opsmonsters.quick_bite.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222"
})
@RequestMapping("/auth/dashboard/home")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardDto> getDashboard(@PathVariable Long userId) {
        DashboardDto dashboardData = dashboardService.getUserDashboard(userId);
        return ResponseEntity.ok(dashboardData);
    }


    @PostMapping("/logout")
    public ResponseEntity<DashboardDto> logoutUser() {

        SecurityContextHolder.clearContext();


        DashboardDto logoutResponse = new DashboardDto(
                null, null, null, null, "Logout successful."
        );

        return ResponseEntity.ok(logoutResponse);
    }
}