package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.DashboardCounts;
import com.opsmonsters.quick_bite.dto.OrderDto;
import com.opsmonsters.quick_bite.dto.OrderItemDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.models.Order;
import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.repositories.OrderRepo;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import com.opsmonsters.quick_bite.repositories.UserRepo;
import com.opsmonsters.quick_bite.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})
@RequestMapping("/auth/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    public OrderController(OrderService orderService,
                           UserRepo userRepo,
                           ProductRepo productRepo,
                           OrderRepo orderRepo) {
        this.orderService = orderService;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
    }

    // Helper method to get logged-in username
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // ================= Create Order (Cart Checkout) =================
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> placeOrder(@RequestParam Long cartId) {
        String username = getCurrentUsername();
        Long userId = orderService.findUserIdByUsername(username);
        ResponseDto response = orderService.placeOrder(userId, cartId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ================= COD Order (NOW SAVES ADDRESS + MOBILE) =================
    // ================= COD Order (NOW SAVES ADDRESS + MOBILE + ITEMS_JSON) =================
    @PostMapping("/cod")
    public ResponseEntity<Order> placeCodOrder(@RequestBody OrderDto orderDto) {
        Users user = userRepo.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setPaymentMethod("COD");
        order.setStatus("PENDING");
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        order.setDeliveryMobile(orderDto.getMobileNumber());

        List<OrderItemDto> mergedItems = orderService.mergeDuplicates(orderDto.getItems());

        List<Product> products = productRepo.findAllById(
                mergedItems.stream().map(i -> i.getProductId()).toList()
        );
        order.setProducts(products);

        String colors = mergedItems.stream().map(i -> i.getColor()).distinct().toList().toString();
        String sizes  = mergedItems.stream().map(i -> i.getSize()).distinct().toList().toString();

        order.setColorJson(colors);
        order.setSizeJson(sizes);
        order.setItemsJson(new ArrayList<>(mergedItems));

        // ✅ Stock decrement
        for (OrderItemDto item : mergedItems) {
            Product p = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            int newStock = p.getStock() - item.getQuantity();
            if (newStock < 0) newStock = 0;
            p.setStock(newStock);
            productRepo.save(p);
        }

        return ResponseEntity.ok(orderRepo.save(order));
    }



    // ================= Admin: Get All Orders =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ================= User: Get Orders =================
    @GetMapping("/user")
    public ResponseEntity<Page<OrderDto>> getUserOrders(
            @RequestParam int page,
            @RequestParam int size) {

        String username = getCurrentUsername();
        Long userId = orderService.findUserIdByUsername(username);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderDto> orders = orderService.getUserOrders(userId, pageRequest);
        return ResponseEntity.ok(orders);
    }

    // ================= Get Order By ID =================
    // ================= Get Order By ID =================
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // ================= Update Order =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}")
    public ResponseEntity<ResponseDto> updateOrder(
            @PathVariable String orderId,
            @RequestParam String newPaymentMethod) {

        ResponseDto response = orderService.updateOrder(orderId, newPaymentMethod);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ================= Delete Order =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResponseDto> deleteOrder(@PathVariable String orderId) {
        ResponseDto response = orderService.deleteOrder(orderId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



    // ================= Dashboard Counts (User) =================
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseDto> getUserDashboardCounts() {
        String username = getCurrentUsername();
        Long userId = orderService.findUserIdByUsername(username);
        ResponseDto response = orderService.getUserDashboardCounts(userId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/online")
    public ResponseEntity<ResponseDto> placeOnlineOrder(@RequestBody OrderDto orderDto) {
        String username = getCurrentUsername();
        Long userId = orderService.findUserIdByUsername(username);

        // ✅ Verify Razorpay signature
        boolean isValid = orderService.verifyRazorpaySignature(
                orderDto.getRazorpayOrderId(),
                orderDto.getRazorpayPaymentId(),
                orderDto.getRazorpaySignature(),
                "YOUR_KEY_SECRET"
        );

        if (!isValid) {
            return ResponseEntity.status(400)
                    .body(new ResponseDto(400, "Payment verification failed!", null));
        }

        orderDto.setPaymentMethod("ONLINE");

        ResponseDto response = orderService.placeOnlineOrder(userId, orderDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}
