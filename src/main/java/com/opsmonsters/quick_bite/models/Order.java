package com.opsmonsters.quick_bite.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opsmonsters.quick_bite.dto.OrderItemDto;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private String orderId;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "cart_id", nullable = true)
    private Cart cart;

    @Column(name = "payment_id", nullable = true)
    private String paymentId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod = "COD";

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_product",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    // NEW fields
    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(name = "delivery_mobile")
    private String deliveryMobile;

    @Column(name = "status", nullable = false)
    private String status = "PAID";

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "signature")
    private String signature;

    @Column(name = "color")
    private String colorJson;  // store as comma separated or JSON

    @Column(name = "size")
    private String sizeJson;

    // ✅ Store merged items as JSON string
    @Lob
    @Column(name = "items_json", columnDefinition = "TEXT")
    private String itemsJson;

    // ===== ItemsJson Getters/Setters =====
    public List<OrderItemDto> getItemsJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(itemsJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, OrderItemDto.class));
        } catch (Exception e) {
            return null;
        }
    }

    public void setItemsJson(List<OrderItemDto> items) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.itemsJson = mapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert items to JSON", e);
        }
    }

    public String getSizeJson() {
        return sizeJson;
    }

    public void setSizeJson(String sizeJson) {
        this.sizeJson = sizeJson;
    }

    public String getColorJson() {
        return colorJson;
    }

    public void setColorJson(String colorJson) {
        this.colorJson = colorJson;
    }

    public Order() {}

    public Order(Users user, Cart cart, String paymentMethod) {
        this.user = user;
        this.cart = cart;
        this.paymentMethod = paymentMethod;
    }

    // ✅ Single PrePersist method
    @PrePersist
    protected void prePersist() {
        if (this.orderId == null) {
            this.orderId = UUID.randomUUID().toString();
        }
        this.orderDate = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getDeliveryMobile() { return deliveryMobile; }
    public void setDeliveryMobile(String deliveryMobile) { this.deliveryMobile = deliveryMobile; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
