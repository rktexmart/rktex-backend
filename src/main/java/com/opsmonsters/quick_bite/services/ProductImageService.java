package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.ProductImage;
import com.opsmonsters.quick_bite.repositories.ProductImageRepo;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductImageService {

    private final ProductImageRepo productImageRepository;
    private final ProductRepo productRepository;

    public ProductImageService(ProductImageRepo productImageRepository, ProductRepo productRepository) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
    }
    public Map<String, Object> uploadImage(Long productId, String imageUrl) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return Map.of("message", "Product not found!", "Status_Code", 400);
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return Map.of("message", "Image URL cannot be empty!", "Status_Code", 400);
        }

        Product product = productOpt.get();
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);  // Ensure this is not null before saving

        productImageRepository.save(productImage);

        return Map.of(
                "message", "Image URL saved successfully!",
                "image_url", imageUrl,
                "Status_Code", 200
        );
    }

    public String saveImageFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        String uploadDir = "uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        try {
            Files.copy(file.getInputStream(), Paths.get(uploadDir).resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image file", e);
        }

        return filename;
    }
}

