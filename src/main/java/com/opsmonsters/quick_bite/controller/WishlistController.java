package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})
@RequestMapping("/user/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;


    @GetMapping
    public ResponseEntity<Set<Product>> getWishlist() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Set<Product> wishlist = wishlistService.getWishlist(userEmail);
        return ResponseEntity.ok(wishlist);
    }


    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addToWishlist(@PathVariable Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String message = wishlistService.addToWishlist(userEmail, productId);
        return ResponseEntity.ok(message);
    }


    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        String message = wishlistService.removeFromWishlist(userEmail, productId);
        return ResponseEntity.ok(message);
    }
}