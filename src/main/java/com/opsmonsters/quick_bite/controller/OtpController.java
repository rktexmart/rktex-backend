package com.opsmonsters.quick_bite.controller;
import com.opsmonsters.quick_bite.services.OtpService;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222"
})
@RequestMapping("/auth/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<ResponseDto> generateOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        ResponseDto response = otpService.generateOtp(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ResponseDto> validateOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        try {
            ResponseDto response = otpService.validateOtp(email, otp);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(500, "An unexpected error occurred: " + e.getMessage(), null));
        }
    }


    @DeleteMapping("/clear")
    public ResponseEntity<ResponseDto> clearOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        try {

            ResponseDto response = otpService.clearOtp(email);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(500, "An unexpected error occurred: " + e.getMessage(), null));
        }
    }


}