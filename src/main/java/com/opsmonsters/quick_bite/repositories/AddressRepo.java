package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.Address;
import com.opsmonsters.quick_bite.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepo extends JpaRepository<Address, Long> {
    List<Address> findByUser_UserId(Long userId);
    List<Address> findByUser(Users user);
}