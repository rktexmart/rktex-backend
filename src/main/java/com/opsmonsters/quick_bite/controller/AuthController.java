package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.*;
import com.opsmonsters.quick_bite.services.AuthServices;
import com.opsmonsters.quick_bite.services.BlackListService;
import com.opsmonsters.quick_bite.services.OtpService;
import com.opsmonsters.quick_bite.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222"
})

@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServices authServices;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @Autowired
    private BlackListService blacklistService;

    @PostMapping("/otp")
    public ResponseEntity<ResponseDto> createOtp(@RequestBody OtpDto otpDto) {
        ResponseDto response = otpService.generateOtp(otpDto.getEmail());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            LoginResponseDto response = authServices.userLoginWithDetails(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // console la exact error varum
            return ResponseEntity.status(500)
                    .body("Login failed: " + e.getMessage());
        }
    }
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
//        LoginResponseDto response = authServices.userLoginWithDetails(loginDto);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDto> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        ResponseDto response = authServices.forgotPassword(forgotPasswordDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseDto resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return authServices.resetPassword(resetPasswordDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            blacklistService.blacklistToken(token);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}
