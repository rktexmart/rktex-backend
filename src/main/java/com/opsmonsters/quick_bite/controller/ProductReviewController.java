package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.ReviewRequestDto;
import com.opsmonsters.quick_bite.dto.ReviewResponseDto;
import com.opsmonsters.quick_bite.models.ProductReview;
import com.opsmonsters.quick_bite.services.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222"
})
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    // Post review
    @PostMapping("/users/{productId}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Long productId,
            @RequestBody ReviewRequestDto reviewRequest,
            Authentication authentication) {

        // Check if JWT authentication exists
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in to submit a review.");
        }

        String username = authentication.getName();

        try {
            ProductReview saved = productReviewService.addReview(
                    productId,
                    username,
                    reviewRequest.getRating(),
                    reviewRequest.getReviewText()
            );

            ReviewResponseDto response = new ReviewResponseDto();
            response.setMessage("Review added successfully");
            response.setProductId(productId);
            response.setRating(saved.getRating());
            response.setReviewText(saved.getReviewText());
            response.setUsername(saved.getUsername());

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            // ✅ Detailed error for EC2 logs
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save review: " + e.getMessage());
        }
    }

    @GetMapping("/users/{productId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long productId) {
        double averageRating = productReviewService.getAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/users/{productId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long productId) {
        List<ReviewResponseDto> reviews = productReviewService.getReviews(productId);
        return ResponseEntity.ok(reviews);
    }

}
