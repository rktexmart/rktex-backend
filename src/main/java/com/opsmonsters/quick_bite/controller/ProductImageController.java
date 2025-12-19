package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.services.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222",
        "https://heartfelt-blancmange-2c2c34.netlify.app",
        "https://rktex-frontend.netlify.app"
})
@RequestMapping("/product/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            String filename = productImageService.saveImageFile(file);
            String imageUrl = "https://rktex-backend.onrender.com/uploads/" + filename;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Image upload failed"));
        }
    }



    @PostMapping("/link/{productId}")
    public ResponseEntity<Map<String, Object>> linkImageToProduct(@PathVariable Long productId,
                                                                  @RequestBody Map<String, String> requestBody) {
        String imageUrl = requestBody.get("imageUrl");

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Image URL cannot be empty!",
                    "Status_Code", 400
            ));
        }

        Map<String, Object> response = productImageService.uploadImage(productId, imageUrl);
        return ResponseEntity.status((int) response.get("Status_Code")).body(response);
    }
}



