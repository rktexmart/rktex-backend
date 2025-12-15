package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.Users;
import com.opsmonsters.quick_bite.models.Wishlist;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import com.opsmonsters.quick_bite.repositories.UserRepo;
import com.opsmonsters.quick_bite.repositories.WishlistRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class WishlistService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;


    @Autowired
    private WishlistRepo wishlistRepo;


    public Set<Product> getWishlist(String userEmail) {
        Users user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // force initialize wishlist
        user.getWishlist().size();
        return user.getWishlist();
    }



    public String addToWishlist(String userEmail, Long productId) {
        Users user = userRepo.findByEmail(userEmail).orElseThrow();
        Product product = productRepo.findById(productId).orElseThrow();

        Wishlist w = new Wishlist();
        w.setUser(user);
        w.setProduct(product);

        wishlistRepo.save(w);
        return "Product added to wishlist";
    }

    public String removeFromWishlist(String userEmail, Long productId) {
        Users user = userRepo.findByEmail(userEmail).orElseThrow();
        wishlistRepo.deleteByProduct_ProductId(productId);
        return "Product removed from wishlist";
    }

}