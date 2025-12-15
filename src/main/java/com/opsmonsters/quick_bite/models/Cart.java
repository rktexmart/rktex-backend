package com.opsmonsters.quick_bite.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Allow guest users
    private Users user;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CartDetails> cartDetails = new ArrayList<>();

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount = 0.0; // Default no discount applied

    @Column(name = "discount_applied", nullable = false)
    private boolean discountApplied = false; // Track if discount has been applied

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "promo_code_id", referencedColumnName = "promo_code_id", nullable = true)
    private PromoCode promoCode;


    public Cart() {} // 🔹 Default constructor for JPA

    public Cart(Users user, String status) {
        this.user = user;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    public Long getCartId() {
        return cartId;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<CartDetails> getCartDetails() {
        return cartDetails;
    }

    public void addCartDetail(CartDetails detail) {
        cartDetails.add(detail);
        detail.setCart(this);
    }

    public void removeCartDetail(CartDetails detail) {
        cartDetails.remove(detail);
        detail.setCart(null);
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public boolean isDiscountApplied() {
        return discountApplied;
    }

    public void applyDiscount(double discount) {
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        this.discountAmount = discount;
        this.discountApplied = true;
    }

    public void setDiscountApplied(boolean discountApplied) {
        this.discountApplied = discountApplied;
    }

    public double getGrandTotalPrice() {
        double totalPrice = cartDetails.stream()
                .mapToDouble(CartDetails::getTotalPrice)
                .sum();

        return Math.max(totalPrice - discountAmount, 0); // Ensure total doesn't go negative
    }

    public PromoCode getPromoCode() { // 🔹 Getter for PromoCode
        return promoCode;
    }

    public void setPromoCode(PromoCode promoCode) { // 🔹 Setter for PromoCode
        this.promoCode = promoCode;
    }
}