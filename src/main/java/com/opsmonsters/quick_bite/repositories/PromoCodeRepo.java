package com.opsmonsters.quick_bite.repositories;

import com.opsmonsters.quick_bite.models.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoCodeRepo extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByPromoName(String promoName);
    void deleteByProduct_ProductId(Long productId);




    Optional<PromoCode> findByPromoNameAndProduct_ProductId(String promoName, Long productId);

}