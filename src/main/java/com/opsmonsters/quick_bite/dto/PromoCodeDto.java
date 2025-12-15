package com.opsmonsters.quick_bite.dto;

import com.opsmonsters.quick_bite.models.PromoCode;
import java.util.Date;

public class PromoCodeDto {

    private Long promoCodeId;
    private String promoName;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    private Double totalAmount;
    private Long productId;
    private double couponAmount;
    private Double minOrderAmount;
    private Date startDate;
    private Date expiryDate;

    public PromoCodeDto() {
    }

    public PromoCodeDto(Long promoCodeId, String promoName,  Long productId, double couponAmount, Double minOrderAmount, Date startDate, Date expiryDate) {
        this.promoCodeId = promoCodeId;
        this.promoName = promoName;

        this.productId = productId;
        this.couponAmount = couponAmount;
        this.minOrderAmount = minOrderAmount;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
    }


    public PromoCodeDto(PromoCode promoCode) {
        this.promoCodeId = promoCode.getPromoCodeId();
        this.promoName = promoCode.getPromoName();

        this.productId = promoCode.getProduct() != null ? promoCode.getProduct().getProductId() : null;
        this.couponAmount = promoCode.getCouponAmount();
        this.minOrderAmount = promoCode.getMinOrderAmount();
        this.startDate = promoCode.getStartDate();
        this.expiryDate = promoCode.getExpiryDate();
    }

    public Long getPromoCodeId() {
        return promoCodeId;
    }

    public void setPromoCodeId(Long promoCodeId) {
        this.promoCodeId = promoCodeId;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public Double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}