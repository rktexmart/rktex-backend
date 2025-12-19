package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.AddressDto;
import com.opsmonsters.quick_bite.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/auth/userprofile/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/{userId}")
    public ResponseEntity<AddressDto> addAddress(@PathVariable Long userId, @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(addressService.addAddress(userId, addressDto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressDto>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@PathVariable Long addressId, @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressDto));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted successfully");
    }
}