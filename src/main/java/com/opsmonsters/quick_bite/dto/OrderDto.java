package com.opsmonsters.quick_bite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opsmonsters.quick_bite.models.Order;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    // Razorpay Order ID (String) for online payments
    private String orderId;

    private Long userId;
    private Long cartId;
    private List<Long> productIds;

    private String paymentMethod;
    private String paymentId;
    private String deliveryAddress;
    private String mobileNumber;
    private Double totalAmount;
    private String status;

    private String formattedOrderDate;
    private List<ProductDto> products;
    private String signature;
    @JsonProperty("razorpayOrderId")
    private String razorpayOrderId;

    @JsonProperty("razorpayPaymentId")
    private String razorpayPaymentId;

    @JsonProperty("razorpaySignature")
    private String razorpaySignature;

    public List<OrderItemDto> getItems() {
        return items;
    }

    private List<OrderItemDto> items = new ArrayList<>();

    private String colorJson;
    private String sizeJson;

    public String getColorJson() {
        return colorJson;
    }

    public void setColorJson(String colorJson) {
        this.colorJson = colorJson;
    }

    public String getSizeJson() {
        return sizeJson;
    }

    public void setSizeJson(String sizeJson) {
        this.sizeJson = sizeJson;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }// 👈 instead of just productIds


    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderDto() {}

    // Constructor used in service conversion
    public OrderDto(String orderId, Long userId, Long cartId, String paymentMethod, LocalDateTime orderDate, List<ProductDto> products) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartId = cartId;
        this.paymentMethod = paymentMethod;
        this.formattedOrderDate = (orderDate != null) ? orderDate.format(formatter) : null;
        this.products = products;
    }

    // Constructor from Order entity
    // Constructor from Order entity
    public OrderDto(Order order) {
        // Use Razorpay order ID for online payments
        this.orderId = order.getRazorpayOrderId();
        this.userId = order.getUser() != null ? order.getUser().getUserId() : null;
        this.cartId = order.getCart() != null ? order.getCart().getCartId() : null;
        this.paymentMethod = order.getPaymentMethod();
        this.paymentId = order.getPaymentId();
        this.formattedOrderDate = (order.getOrderDate() != null) ? order.getOrderDate().format(formatter) : null;

        this.products = order.getProducts() != null
                ? order.getProducts().stream()
                .map(p -> new ProductDto(
                        p.getProductId(),
                        p.getName(),
                        p.getImageFilename(),
                        p.getMrp(),
                        p.getDiscount(),
                        p.getDescription(),
                        p.getAbout(),
                        p.getTags(),
                        p.getStock(),
                        0.0 // ✅ Double instead of int
                ))
                .collect(java.util.stream.Collectors.toList()) // ✅ safer for Java 8+
                : null;

        this.deliveryAddress = order.getDeliveryAddress();
        this.mobileNumber = order.getDeliveryMobile();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.signature = order.getSignature(); // optional, if stored
    }


    // ===== Getters & Setters =====
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFormattedOrderDate() { return formattedOrderDate; }
    public void setFormattedOrderDate(String formattedOrderDate) { this.formattedOrderDate = formattedOrderDate; }

    public List<ProductDto> getProducts() { return products; }
    public void setProducts(List<ProductDto> products) { this.products = products; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
