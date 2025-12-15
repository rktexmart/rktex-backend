package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.Wishlist;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepo extends JpaRepository<Wishlist, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.product.productId = :productId")
    void deleteByProduct_ProductId(@Param("productId") Long productId);
}


