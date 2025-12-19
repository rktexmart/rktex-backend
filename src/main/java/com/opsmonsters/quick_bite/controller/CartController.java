package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.CartDetailsDto;
import com.opsmonsters.quick_bite.dto.CartDto;
import com.opsmonsters.quick_bite.dto.ResponseDto;
import com.opsmonsters.quick_bite.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. Add product to cart
    @PostMapping("/add")
    public ResponseEntity<ResponseDto> addToCart(@RequestBody CartDto cartDto) {
        ResponseDto response = cartService.addToCart(cartDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 2. Remove single product from cart
    @DeleteMapping("/remove/{cartDetailsId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartDetailsId) {
        cartService.removeFromCart(cartDetailsId);
        return ResponseEntity.noContent().build(); // 👈 204 success
    }

    // 3. Get cart DETAILS by cartId (items + totals)
    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartDetails(@PathVariable Long cartId) {
        CartDetailsDto cartDetails = cartService.getCartDetails(cartId);
        return (cartDetails == null)
                ? ResponseEntity.status(404).body("Cart not found!")
                : ResponseEntity.ok(cartDetails);
    }

    // 4. Get ALL user cart items (wraps CartDetailsDto)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserCartItems(@PathVariable Long userId) {
        CartDto activeCart = cartService.getCartByUserId(userId);
        if (activeCart == null || activeCart.getCartId() == null) {
            return ResponseEntity.status(404).body("No active cart!");
        }

        CartDetailsDto details = cartService.getCartDetails(activeCart.getCartId());
        if (details == null || details.getProducts() == null || details.getProducts().isEmpty()) {
            return ResponseEntity.status(404).body("No items in your cart!");
        }

        return ResponseEntity.ok(details); // 🔑 full details with products + totals
    }

    // 5. Get ACTIVE cart meta only (CartDto)
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveCart(@PathVariable Long userId) {
        CartDto active = cartService.getCartByUserId(userId);
        if (active == null || active.getCartId() == null) {
            return ResponseEntity.status(404).body("No active cart!");
        }
        return ResponseEntity.ok(active); // 🔑 returns only cart meta
    }

    // 6. Get ACTIVE cart details (items + totals)
    @GetMapping("/user/{userId}/active/details")
    public ResponseEntity<?> getActiveCartDetails(@PathVariable Long userId) {
        CartDto active = cartService.getCartByUserId(userId);
        if (active == null || active.getCartId() == null) {
            return ResponseEntity.status(404).body("No active cart!");
        }
        CartDetailsDto details = cartService.getCartDetails(active.getCartId());
        return ResponseEntity.ok(details);
    }
}
