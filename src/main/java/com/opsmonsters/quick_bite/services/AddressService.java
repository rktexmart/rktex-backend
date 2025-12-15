package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.AddressDto;
import com.opsmonsters.quick_bite.models.Address;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.repositories.AddressRepo;

import com.opsmonsters.quick_bite.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private AddressRepo addressRepository;

    @Autowired
    private UserRepo userRepo;

    public AddressDto addAddress(Long userId, AddressDto addressDto) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = new Address();
        address.setUser(user);
        address.setFullName(addressDto.getFullName());
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCountry(addressDto.getCountry());
        address.setDefault(addressDto.isDefault());

        Address savedAddress = addressRepository.save(address);
        return convertToDto(savedAddress);
    }

    public List<AddressDto> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUser_UserId(userId);
        return addresses.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setFullName(addressDto.getFullName());
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCountry(addressDto.getCountry());
        address.setDefault(addressDto.isDefault());

        Address updatedAddress = addressRepository.save(address);
        return convertToDto(updatedAddress);
    }

    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    private AddressDto convertToDto(Address address) {
        return new AddressDto(
                address.getAddressId(),
                address.getFullName(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault()
        );
    }
}