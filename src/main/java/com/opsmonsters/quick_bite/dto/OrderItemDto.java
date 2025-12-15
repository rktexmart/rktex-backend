package com.opsmonsters.quick_bite.dto;

public class OrderItemDto {
    private Long productId;
    private Integer quantity;
    private String size;
    private String color;

    // ✅ No-args constructor required by Jackson
    public OrderItemDto() {
    }

    public OrderItemDto(Long productId, Integer quantity, String size, String color) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }

    // ✅ Getters & Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
