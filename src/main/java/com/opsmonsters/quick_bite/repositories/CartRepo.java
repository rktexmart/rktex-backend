package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.Cart;
import com.opsmonsters.quick_bite.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findFirstByUserAndStatus(Users user, String status); // for active cart

    List<Cart> findByUser(Users user); // all carts by user

    List<Cart> findAllByUserAndStatus(Users user, String status); // filtered carts by status

    long countByUser_UserId(Long userId); // useful for stats
}
