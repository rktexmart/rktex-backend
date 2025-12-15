package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
}

