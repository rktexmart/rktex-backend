package com.opsmonsters.quick_bite.controller;

import com.opsmonsters.quick_bite.dto.ProductAttributeDto;
import com.opsmonsters.quick_bite.models.ProductAttribute;
import com.opsmonsters.quick_bite.services.ProductAttributeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-attributes")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "http://13.61.26.222"
})
public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    public ProductAttributeController(ProductAttributeService attributeService) {
        this.attributeService = attributeService;
    }

    // Admin only - add attributes
    @PostMapping("/{productId}")
    public List<ProductAttribute> addAttributes(
            @PathVariable Long productId,
            @RequestBody List<ProductAttributeDto> dtos) {
        return attributeService.addAttributes(productId, dtos);
    }

    // User - get attributes
    @GetMapping("/{productId}")
    public List<ProductAttributeDto> getAttributes(@PathVariable Long productId) {
        return attributeService.getAttributes(productId);
    }

    // Admin only - delete attributes
    @DeleteMapping("/{productId}")
    public void deleteAttributes(@PathVariable Long productId) {
        attributeService.deleteAttributes(productId);
    }
}
