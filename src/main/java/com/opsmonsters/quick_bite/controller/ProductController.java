package com.opsmonsters.quick_bite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opsmonsters.quick_bite.dto.ProductAttributeDto;
import com.opsmonsters.quick_bite.dto.ProductDto;
import com.opsmonsters.quick_bite.models.ProductReview;
import com.opsmonsters.quick_bite.services.ProductReviewService;
import com.opsmonsters.quick_bite.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"

})
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ProductReviewService ratingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> createProduct(
            @RequestParam("name") String name,
            @RequestParam("mrp") Double mrp,
            @RequestParam("discount") Double discount,
            @RequestParam("description") String description,
            @RequestParam(value = "about", required = false) String about,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "attributes", required = false) String attributesJson
    ) {
        ProductDto dto = new ProductDto();
        dto.setName(name);
        dto.setMrp(mrp);
        dto.setDiscount(discount);
        dto.setDescription(description);
        dto.setAbout(about);
        dto.setStock(stock);
        dto.setImageUrl(saveImageFile(image, request));

        // ✅ Safe parsing with try-catch
        if (attributesJson != null && !attributesJson.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<ProductAttributeDto> attrs = Arrays.asList(
                        mapper.readValue(attributesJson, ProductAttributeDto[].class)
                );
                dto.setAttributes(attrs);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null); // parsing failed
            }
        }

        ProductDto savedProduct = productService.createProduct(dto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
//
//    @GetMapping
//    public List<ProductDto> getAllProductsAlias() {
//        return productService.getAllProducts();
//    }


    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }
    @GetMapping("/{id}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long id) {
        double avg = ratingService.getAverageRating(id);
        return ResponseEntity.ok(avg);
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Long id,
            @RequestParam int rating,
            @RequestParam String reviewText,
            Authentication authentication   // ✅ add this
    ) {
        String username = authentication.getName(); // ✅ username from JWT
        ProductReview review = ratingService.addReview(id, username, rating, reviewText);

        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }



    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId,
                                                    @RequestParam String name,
                                                    @RequestParam double mrp,
                                                    @RequestParam double discount,
                                                    @RequestParam String description,
                                                    @RequestParam String about,
                                                    @RequestParam int stock,
                                                    @RequestParam(required = false) MultipartFile image,
                                                    @RequestParam(value = "attributes", required = false) String attributesJson
    ) {
        ProductDto dto = new ProductDto();
        dto.setName(name);
        dto.setMrp(mrp);
        dto.setDiscount(discount);
        dto.setDescription(description);
        dto.setAbout(about);
        dto.setStock(stock);

        if (image != null && !image.isEmpty()) {
            dto.setImageUrl(saveImageFile(image, request));
        }

        // ✅ Safe parsing for update
        if (attributesJson != null && !attributesJson.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<ProductAttributeDto> attrs = Arrays.asList(
                        mapper.readValue(attributesJson, ProductAttributeDto[].class)
                );
                dto.setAttributes(attrs);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }

        ProductDto updated = productService.updateProduct(productId, dto);
        return ResponseEntity.ok(updated);
    }

    private String saveImageFile(MultipartFile file, HttpServletRequest request) {
        if (file == null) return null;

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
