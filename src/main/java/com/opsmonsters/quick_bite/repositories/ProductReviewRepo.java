package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReviewRepo extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProduct_ProductId(Long productId);
    void deleteByProduct_ProductId(Long productId);// Correct method to find by product ID
}
