package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.*;
import com.opsmonsters.quick_bite.models.*;
import com.opsmonsters.quick_bite.repositories.*;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final ProductReviewService productReviewService;
    private final String RAZORPAY_KEY_SECRET = "YOUR_KEY_SECRET";

    public OrderService(OrderRepo orderRepo, CartRepo cartRepo, UserRepo userRepo,
                        ProductRepo productRepo, ProductReviewService productReviewService) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.productReviewService = productReviewService;
    }

    // ------------------- CART to COD -------------------
    @Transactional
    public ResponseDto placeOrder(Long userId, Long cartId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found!"));

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart with ID " + cartId + " not found!"));

        Order order = new Order(user, cart, "Cash on Delivery");

        List<CartDetails> cartItems = cart.getCartDetails();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty!");
        }

        List<Product> orderedProducts = new ArrayList<>();
        for (CartDetails item : cartItems) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            if (product.getStock() < quantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - quantity);
            productRepo.save(product);
            orderedProducts.add(product);
        }

        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getMrp() * item.getQuantity())
                .sum();
        order.setTotalAmount(total);

        order.setProducts(orderedProducts);
        orderRepo.save(order);

        cartRepo.delete(cart);

        return new ResponseDto(200, "Order placed successfully!", convertToDto(order));
    }

    // ------------------- COD DIRECT (no cart) -------------------
    @Transactional
    public ResponseDto placeCodOrder(Long userId, List<OrderItemDto> items,
                                     double totalAmount, String deliveryAddress, String mobileNumber) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found!"));

        if (items == null || items.isEmpty()) {
            return new ResponseDto(400, "No items provided!", null);
        }

        // ✅ Merge duplicates
        List<OrderItemDto> mergedItems = mergeDuplicates(items);

        List<Product> selectedProducts = new ArrayList<>();
        for (OrderItemDto item : mergedItems) {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                return new ResponseDto(400, "Not enough stock for: " + product.getName(), null);
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);
            selectedProducts.add(product);
        }

        Order order = new Order();
        order.setUser(user);
        order.setProducts(selectedProducts);
        order.setPaymentMethod("COD");
        order.setOrderDate(LocalDateTime.now());
        order.setCart(null);
        order.setDeliveryAddress(deliveryAddress);
        order.setDeliveryMobile(mobileNumber);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        // ✅ Store color + size as JSON
        order.setColorJson(mergedItems.stream()
                .map(i -> i.getProductId() + ":" + i.getColor())
                .collect(Collectors.joining(",")));

        order.setSizeJson(mergedItems.stream()
                .map(i -> i.getProductId() + ":" + i.getSize())
                .collect(Collectors.joining(",")));

        orderRepo.save(order);

        return new ResponseDto(200, "COD Order placed successfully!", convertToDto(order));
    }

    // ------------------- ONLINE ORDER -------------------
    // ------------------- ONLINE ORDER -------------------
    public ResponseDto placeOnlineOrder(Long userId, OrderDto orderDto) {
        // ✅ Merge duplicate items (same product, same color & size)
        List<OrderItemDto> mergedItems = mergeDuplicates(orderDto.getItems());

        // ✅ Decrement stock
        for (OrderItemDto item : mergedItems) {
            Product p = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            int newStock = p.getStock() - item.getQuantity();
            if (newStock < 0) newStock = 0;
            p.setStock(newStock);
            productRepo.save(p);
        }

        // ✅ Save Order
        Order order = new Order();
        order.setUser(userRepo.findById(userId).orElseThrow());
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setPaymentMethod("ONLINE");
        order.setStatus("PAID");
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryMobile(orderDto.getMobileNumber());
        order.setProducts(productRepo.findAllById(
                mergedItems.stream().map(i -> i.getProductId()).toList()
        ));
        order.setItemsJson(new ArrayList<>(mergedItems));
        order.setColorJson(mergedItems.stream().map(i -> i.getColor()).distinct().toList().toString());
        order.setSizeJson(mergedItems.stream().map(i -> i.getSize()).distinct().toList().toString());

        orderRepo.save(order);
        return new ResponseDto(200, "Order placed successfully!", order);
    }

    // ✅ Merge duplicates helper
    public List<OrderItemDto> mergeDuplicates(List<OrderItemDto> items) {
        Map<String, OrderItemDto> map = new HashMap<>();
        for (OrderItemDto item : items) {
            String key = item.getProductId() + "-" + item.getColor() + "-" + item.getSize();
            if (map.containsKey(key)) {
                OrderItemDto existing = map.get(key);
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                map.put(key, item);
            }
        }
        return new ArrayList<>(map.values());
    }



    // ------------------- CRUD + HELPERS -------------------
    public OrderDto getOrderById(String orderId) {
        return orderRepo.findByIdWithProducts(orderId)
                .map(OrderDto::new)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public ResponseDto updateOrder(String orderId, String newPaymentMethod) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentMethod(newPaymentMethod);
        orderRepo.save(order);

        return new ResponseDto(200, "Order updated successfully", new OrderDto(order));
    }

    public ResponseDto deleteOrder(String orderId) {
        if (!orderRepo.existsById(orderId)) {
            return new ResponseDto(404, "Order not found", null);
        }
        orderRepo.deleteById(orderId);
        return new ResponseDto(200, "Order deleted successfully", null);
    }

    public OrderDto convertToDto(Order order) {
        List<ProductDto> productDtos = order.getProducts() != null
                ? order.getProducts().stream()
                .map(product -> new ProductDto(
                        product.getProductId(),
                        product.getName(),
                        product.getImageFilename(),
                        product.getMrp(),
                        product.getDiscount(),
                        product.getDescription(),
                        product.getAbout(),
                        product.getTags(),
                        product.getStock(),
                        productReviewService.getAverageRating(product.getProductId())
                ))
                .collect(Collectors.toList())
                : null;

        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setCartId(order.getCart() != null ? order.getCart().getCartId() : null);
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setFormattedOrderDate(
                order.getOrderDate() != null
                        ? order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                        : null
        );
        dto.setProducts(productDtos);

        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setMobileNumber(order.getDeliveryMobile());
        dto.setStatus(order.getStatus());
        dto.setPaymentId(order.getPaymentId());
        dto.setRazorpayOrderId(order.getRazorpayOrderId());
        dto.setRazorpayPaymentId(order.getRazorpayOrderId());
        dto.setRazorpaySignature(order.getSignature());

        dto.setColorJson(order.getColorJson());
        dto.setSizeJson(order.getSizeJson());

        // ✅ Add this to include items
        if (order.getItemsJson() != null) {
            dto.setItems(order.getItemsJson());
        }

        return dto;
    }


    public ResponseDto getUserDashboardCounts(Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found!"));

        long orderCount = orderRepo.countByUser_UserId(userId);
        long cartCount = cartRepo.countByUser_UserId(userId);

        return new ResponseDto(200, "Dashboard data fetched successfully!", new DashboardCounts(orderCount, cartCount));
    }

    public Long findUserIdByUsername(String username) {
        return userRepo.findByEmail(username)
                .map(Users::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username/email: " + username));
    }

    public List<OrderDto> getAllOrders() {
        return orderRepo.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public boolean verifyRazorpaySignature(String orderId, String paymentId, String signature, String keySecret) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            Utils.verifyPaymentSignature(options, keySecret);
            return true;
        } catch (RazorpayException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Page<OrderDto> getUserOrders(Long userId, Pageable pageable) {
        return orderRepo.findByUser_UserId(userId, pageable).map(this::convertToDto);
    }
}
