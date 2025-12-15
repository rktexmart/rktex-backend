package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.models.Otp;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.repositories.OtpRepo;
import com.opsmonsters.quick_bite.repositories.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepo otpRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;

    public ResponseDto generateOtp(String email) {
        try {
            Optional<Users> userOptional = userRepo.findByEmail(email);
            if (userOptional.isEmpty()) {
                return new ResponseDto(400, "User not found with the provided email", null);
            }

            Users user = userOptional.get();
            String otp = String.format("%06d", new SecureRandom().nextInt(999999));

            Otp otpEntity = new Otp();
            otpEntity.setUser(user);
            otpEntity.setOtp(otp);
            otpEntity.setCreatedAt(new Date());
            otpEntity.setExpiresAt(new Date(System.currentTimeMillis() + 300000));
            otpEntity.setIsUsed(false);

            otpRepo.save(otpEntity);

            // ✅ Send email to customer
            emailService.sendOtpEmail(user.getEmail(), otp);

            return new ResponseDto(200, "OTP generated & sent to email successfully", null);

        } catch (Exception e) {
            return new ResponseDto(500, "Error generating OTP: " + e.getMessage(), null);
        }
    }

    public ResponseDto validateOtp(String email, String otp) {
        try {

            Users user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with the provided email"));


            Optional<Otp> otpOptional = otpRepo.findByUserAndOtp(user, otp);


            if (otpOptional.isEmpty()) {
                return new ResponseDto(400, "Invalid OTP", null);
            }

            Otp otpEntity = otpOptional.get();


            if (otpEntity.getIsUsed()) {
                return new ResponseDto(400, "OTP already used", null);
            }


            if (new Date().after(otpEntity.getExpiresAt())) {
                return new ResponseDto(400, "OTP expired", null);
            }


            otpEntity.setIsUsed(true);
            otpRepo.save(otpEntity);


            user.setIsOtpVerified(true);
            userRepo.save(user);

            return new ResponseDto(200, "OTP validated and verified successfully", null);

        } catch (Exception e) {
            return new ResponseDto(500, "Error while validating OTP: " + e.getMessage(), null);
        }
    }
    @Transactional
    public ResponseDto clearOtp(String email) {
        try {

            Users user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with the provided email"));


            otpRepo.deleteByUser(user);

            return new ResponseDto(200, "All OTPs for user with email " + email + " have been cleared successfully.");
        } catch (Exception e) {

            return new ResponseDto(500, "An error occurred while clearing OTPs: " + e.getMessage());
        }
    }

}