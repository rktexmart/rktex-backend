package com.opsmonsters.quick_bite.services;
import com.opsmonsters.quick_bite.dto.*;
import com.opsmonsters.quick_bite.models.Otp;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.repositories.OtpRepo;
import com.opsmonsters.quick_bite.repositories.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthServices {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtServices jwtService;
    private final OtpRepo otpRepo;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private static final Logger logger = LoggerFactory.getLogger(AuthServices.class);


    public AuthServices(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder,
            JwtServices jwtService,
            OtpRepo otpRepo,
            OtpService otpService,
            AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.otpRepo= otpRepo;
        this.jwtService = jwtService;
        this.otpService= otpService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseDto userLogin(LoginDto loginDto) {
        try {
            System.out.println(loginDto.getEmail());
            logger.info("Attempting to login with username: {}", loginDto.getEmail());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );


            Optional<Users> userOptional = userRepo.findByEmail(loginDto.getEmail());


            if (userOptional.isEmpty()) {
                logger.error("User not found with username: {}", loginDto.getEmail());
                return new ResponseDto(404, "Email does not exist");
            }

            Users user = userOptional.get();

            if (!user.getIsOtpVerified()) {
                logger.error("User with email {} has not verified OTP", loginDto.getEmail());
                return new ResponseDto(403, "Please verify your OTP before logging in.");
            }


            String jwtToken = jwtService.generateToken(user.getUsername(), user.getRole());
            logger.info("User logged in successfully: {}", user.getUsername());

            return new ResponseDto(200, jwtToken);

        } catch (BadCredentialsException badCredentials) {
            badCredentials.printStackTrace(System.out);
            logger.error("Invalid credentials for username: {}", loginDto.getEmail());
            return new ResponseDto(403, "Username / password is incorrect");
        } catch (Exception e) {

            logger.error("An error occurred during login: {}", e.getMessage());
            return new ResponseDto(500, "An internal error occurred");
        }
    }
    public String authenticate(String email, String password) {
        logger.info("Authenticating user with email: {}", email);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            logger.error("Authentication failed for email: {}", email);
            throw new RuntimeException("Invalid username or password");
        }


        Users user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsOtpVerified()) {
            logger.error("User with email {} has not verified OTP", email);
            throw new RuntimeException("Please verify your OTP before logging in.");
        }

        return jwtService.generateToken(user.getEmail(), user.getRole());
    }

    public ResponseDto forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        try {
            logger.info("Processing forgot password request for email: {}", forgotPasswordDto.getEmail());


            ResponseDto otpResponse = otpService.generateOtp(forgotPasswordDto.getEmail());

            if (otpResponse.getStatusCode() != 200) {
                logger.error("Error generating OTP for email: {}", forgotPasswordDto.getEmail());
                return otpResponse;
            }


            logger.info("OTP generated successfully for email: {}", forgotPasswordDto.getEmail());
            return new ResponseDto(200, "OTP generated and sent successfully.");

        } catch (Exception e) {
            logger.error("An error occurred during forgot password processing: {}", e.getMessage());
            return new ResponseDto(500, "An internal error occurred.");
        }
    }


    public ResponseDto resetPassword(ResetPasswordDto resetPasswordDto) {
        try {
            logger.info("Attempting to reset password for email: {}", resetPasswordDto.getEmail());

            Users user = userRepo.findByEmail(resetPasswordDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Otp> otpOptional = otpRepo.findByUserAndOtp(user, resetPasswordDto.getOtp());
            if (otpOptional.isEmpty()) {
                logger.error("Invalid OTP for email: {}", resetPasswordDto.getEmail());
                return new ResponseDto(403, "Invalid OTP.");
            }

            Otp otp = otpOptional.get();

            if (otp.getIsUsed()) {
                logger.error("OTP already used for email: {}", resetPasswordDto.getEmail());
                return new ResponseDto(403, "OTP already used.");
            }

            if (otp.getExpiresAt().before(new Date())) {
                logger.error("OTP expired for email: {}", resetPasswordDto.getEmail());
                return new ResponseDto(403, "OTP expired.");
            }

            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userRepo.save(user);

            otp.setIsUsed(true);
            otpRepo.save(otp);

            logger.info("Password reset successfully for email: {}", resetPasswordDto.getEmail());
            return new ResponseDto(200, "Password reset successfully.");

        } catch (Exception e) {
            logger.error("An error occurred during password reset: {}", e.getMessage());
            return new ResponseDto(500, "An internal error occurred.");
        }
    }

    public LoginResponseDto userLoginWithDetails(LoginDto loginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            Users user = userRepo.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getIsOtpVerified()) {
                throw new RuntimeException("Please verify your OTP before logging in.");
            }

            String jwtToken = jwtService.generateToken(user.getEmail(), user.getRole());

            return new LoginResponseDto(jwtToken, user.getUserId(), user.getEmail());

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Username / password is incorrect");
        }
    }


}