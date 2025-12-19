package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.dto.UserDto;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.services.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController

@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})

public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/auth/register")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserDto userDto) {
        ResponseDto response = userService.createUser(userDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping ("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> getUserById(@PathVariable Long userId) {
        ResponseDto response = userService.getUserById(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PutMapping("/auth/users/{userId}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable Long userId, @RequestBody UserDto dto) {
        ResponseDto response = userService.updateUser(userId, dto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable Long userId) {
        ResponseDto response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}