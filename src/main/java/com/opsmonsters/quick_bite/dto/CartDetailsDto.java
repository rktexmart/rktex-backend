package com.opsmonsters.quick_bite.dto;

import java.util.List;

public class CartDetailsDto {
    private Long cartDetailsId;
    private List<ProductDetails> products;
    private double subTotal;
    private double discountTotal;
    private double tax;
    private double total;

    public CartDetailsDto(List<ProductDetails> products, double subTotal, double discountTotal, double tax, double total) {
        this.products = products;
        this.subTotal = subTotal;
        this.discountTotal = discountTotal;
        this.tax = tax;
        this.total = total;
    }

    public Long getCartDetailsId() {
        return cartDetailsId;
    }

    public void setCartDetailsId(Long cartDetailsId) {
        this.cartDetailsId = cartDetailsId;
    }

    public List<ProductDetails> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDetails> products) {
        this.products = products;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(double discountTotal) {
        this.discountTotal = discountTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // 🔹 Product details DTO
    public static class ProductDetails {
        private Long cartDetailId;
        private String imageUrl;
        private String name;
        private String description;
        private double price;
        private int quantity;
        private String promoName;
        private double discountAmount;
        private String color;
        private String size;

        public ProductDetails(Long cartDetailId, String imageUrl, String name, String description,
                              double price, int quantity, String promoName,
                              double discountAmount, String color, String size) {
            this.cartDetailId = cartDetailId;
            this.imageUrl = imageUrl;
            this.name = name;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.promoName = promoName;
            this.discountAmount = discountAmount;
            this.color = color;
            this.size = size;
        }

        // ✅ Getters
        public Long getCartDetailId() { return cartDetailId; }
        public String getImageUrl() { return imageUrl; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getPromoName() { return promoName; }
        public double getDiscountAmount() { return discountAmount; }
        public String getColor() { return color; }
        public String getSize() { return size; }
    }
}
