package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.Order;
import com.opsmonsters.quick_bite.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, String> { // ✅ ID is String now

    List<Order> findByUser(Users user);
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);
    long countByUser_UserId(Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.products WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithProducts(@Param("orderId") String orderId); // ✅ String
}
