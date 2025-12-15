package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.AddressDto;
import com.opsmonsters.quick_bite.dto.OrderDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.dto.UserDto;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.models.Order;
import com.opsmonsters.quick_bite.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseDto createUser(UserDto dto) {
        try {
            if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
                return new ResponseDto(400, "User with email " + dto.getEmail() + " already exists!");
            }

            Users user = new Users();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setProfileImageUrl(dto.getProfileImageUrl());
            user.setRole(dto.getRole() == null || dto.getRole().isEmpty() ? "USER" : dto.getRole());

            Users savedUser = userRepo.save(user);
            return new ResponseDto(201, "User created successfully!", mapToDto(savedUser));
        } catch (Exception e) {
            return new ResponseDto(500, "Error while creating user: " + e.getMessage(), null);
        }
    }

    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public ResponseDto getUserById(Long userId) {
        return userRepo.findById(userId)
                .map(user -> new ResponseDto(200, "User found", mapToDto(user)))
                .orElse(new ResponseDto(404, "User with ID " + userId + " not found."));
    }

    public ResponseDto updateUser(Long userId, UserDto dto) {
        return userRepo.findById(userId).map(user -> {
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setProfileImageUrl(dto.getProfileImageUrl());
            if (dto.getRole() != null && !dto.getRole().isEmpty()) {
                user.setRole(dto.getRole());
            }
            userRepo.save(user);
            return new ResponseDto(200, "User updated successfully!");
        }).orElse(new ResponseDto(404, "User with ID " + userId + " not found."));
    }

    public Optional<Users> getUserByEmail(String email) {
        try {
            return userRepo.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user by email: " + e.getMessage(), e);
        }
    }

    public ResponseDto deleteUser(Long userId) {
        if (userRepo.existsById(userId)) {
            userRepo.deleteById(userId);
            return new ResponseDto(200, "User deleted successfully!");
        }
        return new ResponseDto(404, "User with ID " + userId + " not found.");
    }

    private UserDto mapToDto(Users user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());


        if (user.getAddresses() != null) {
            dto.setAddresses(user.getAddresses().stream().map(address -> {
                AddressDto addressDto = new AddressDto();
                addressDto.setAddressId(address.getAddressId());
                addressDto.setStreet(address.getStreet());
                addressDto.setCity(address.getCity());
                addressDto.setState(address.getState());
                addressDto.setPostalCode(address.getPostalCode());
                addressDto.setCountry(address.getCountry());
                return addressDto;
            }).collect(Collectors.toList()));
        }


        if (user.getOrders() != null) {
            dto.setOrders(user.getOrders().stream().map(order -> new OrderDto(order)).collect(Collectors.toList()));
        }

        return dto;
    }
}