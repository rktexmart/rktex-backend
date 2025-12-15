package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductAttributeRepo extends JpaRepository<ProductAttribute, Long> {
    // Add this
    List<ProductAttribute> findByProduct_ProductId(Long productId);

    void deleteByProduct_ProductId(Long productId);
}
