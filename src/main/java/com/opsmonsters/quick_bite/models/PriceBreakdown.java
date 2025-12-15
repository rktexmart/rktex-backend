package com.opsmonsters.quick_bite.models;

public class PriceBreakdown {
    private int totalItems;
    private double totalPrice;
    private double tax;
    private double discount;
    private double coupons;
    private double totalAmount;

    // Constructor
    public PriceBreakdown(int totalItems, double totalPrice, double tax, double discount, double coupons, double totalAmount) {
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
        this.tax = tax;
        this.discount = discount;
        this.coupons = coupons;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getCoupons() {
        return coupons;
    }

    public void setCoupons(double coupons) {
        this.coupons = coupons;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
