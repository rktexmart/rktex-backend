package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.ReviewResponseDto;
import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.ProductReview;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import com.opsmonsters.quick_bite.repositories.ProductReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductReviewService {

    private final ProductRepo productRepo;
    private final ProductReviewRepo productReviewRepo;

    @Autowired
    public ProductReviewService(ProductRepo productRepo, ProductReviewRepo productReviewRepo) {
        this.productRepo = productRepo;
        this.productReviewRepo = productReviewRepo;
    }

    public ProductReview addReview(Long productId, String username, int rating, String reviewText) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with ID " + productId + " not found"));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setUsername(username); // ✅ save reviewer name

        return productReviewRepo.save(review);
    }

    public List<ReviewResponseDto> getReviews(Long productId) {
        return productReviewRepo.findByProduct_ProductId(productId)
                .stream()
                .map(review -> {
                    ReviewResponseDto dto = new ReviewResponseDto();
                    dto.setProductId(productId);
                    dto.setRating(review.getRating());
                    dto.setReviewText(review.getReviewText());
                    dto.setUsername(review.getUsername()); // ✅ include username
                    return dto;
                })
                .toList();
    }



    public double getAverageRating(Long productId) {
        // Fetch all reviews for the product
        List<ProductReview> reviews = productReviewRepo.findByProduct_ProductId(productId);

        if (reviews.isEmpty()) {
            return 0.0; // No reviews available, return 0
        }

        // Calculate the average rating
        double sum = reviews.stream().mapToInt(ProductReview::getRating).sum();
        return sum / reviews.size();
    }


}
