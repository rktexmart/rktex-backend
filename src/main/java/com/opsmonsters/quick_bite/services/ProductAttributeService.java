package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.ProductAttributeDto;
import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.ProductAttribute;
import com.opsmonsters.quick_bite.repositories.ProductAttributeRepo;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductAttributeService {

    private final ProductAttributeRepo attributeRepo;
    private final ProductRepo productRepo;

    public ProductAttributeService(ProductAttributeRepo attributeRepo, ProductRepo productRepo) {
        this.attributeRepo = attributeRepo;
        this.productRepo = productRepo;
    }

    // Add attributes
    public List<ProductAttribute> addAttributes(Long productId, List<ProductAttributeDto> dtos) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductAttribute> attributes = dtos.stream().map(dto -> {
            ProductAttribute pa = new ProductAttribute();
            pa.setProduct(product);
            pa.setAttributeName(dto.getAttributeName());
            pa.setAttributeValue(dto.getAttributeValue());
            return pa;
        }).collect(Collectors.toList());

        return attributeRepo.saveAll(attributes);
    }

    // Get attributes
    public List<ProductAttributeDto> getAttributes(Long productId) {
        return attributeRepo.findAll().stream()
                .filter(a -> a.getProduct().getProductId().equals(productId))
                .map(a -> new ProductAttributeDto(a.getAttributeName(), a.getAttributeValue()))
                .collect(Collectors.toList());
    }

    // Delete attributes
    public void deleteAttributes(Long productId) {
        attributeRepo.deleteByProduct_ProductId(productId);
    }
}
