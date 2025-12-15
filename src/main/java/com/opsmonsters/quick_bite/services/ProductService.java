package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.ProductDto;
import com.opsmonsters.quick_bite.dto.ProductAttributeDto;
import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.ProductAttribute;
import com.opsmonsters.quick_bite.models.ProductReview;
import com.opsmonsters.quick_bite.models.Tag;
import com.opsmonsters.quick_bite.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final TagRepo tagRepository;
    private final PromoCodeRepo promoCodeRepo;
    private final ProductReviewRepo productReviewRepo;
    private final WishlistRepo wishlistRepo;
    private final ProductAttributeRepo productAttributeRepo;

    @Autowired
    public ProductService(ProductRepo productRepo,
                          TagRepo tagRepository,
                          PromoCodeRepo promoCodeRepo,
                          ProductReviewRepo productReviewRepo,
                          WishlistRepo wishlistRepo,
                          ProductAttributeRepo productAttributeRepo) {
        this.productRepo = productRepo;
        this.tagRepository = tagRepository;
        this.promoCodeRepo = promoCodeRepo;
        this.productReviewRepo = productReviewRepo;
        this.wishlistRepo = wishlistRepo;
        this.productAttributeRepo = productAttributeRepo;
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        validateProduct(dto);

        Product product = new Product();
        product.setName(dto.getName().trim());
        product.setImageFilename(
                dto.getImageUrl() != null && dto.getImageUrl().startsWith("http")
                        ? dto.getImageUrl().substring(dto.getImageUrl().lastIndexOf("/") + 1)
                        : dto.getImageUrl()
        );
        product.setMrp(dto.getMrp());
        product.setDiscount(dto.getDiscount());
        product.setDescription(dto.getDescription());
        product.setAbout(dto.getAbout());
        product.setStock(dto.getStock());

        Product savedProduct = productRepo.save(product);

        // ✅ Save ProductAttributes
        if (dto.getAttributes() != null) {
            for (ProductAttributeDto attrDto : dto.getAttributes()) {
                ProductAttribute attr = new ProductAttribute();
                attr.setProduct(savedProduct);
                attr.setAttributeName(attrDto.getAttributeName());
                attr.setAttributeValue(attrDto.getAttributeValue());
                productAttributeRepo.save(attr);
            }
        }

        Set<Tag> managedTags = manageTags(dto.getTags());
        savedProduct.setTags(managedTags);

        savedProduct = productRepo.save(savedProduct);
        return convertToDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long productId, ProductDto dto) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with ID " + productId + " not found"));

        product.setName(dto.getName());
        product.setImageFilename(
                dto.getImageUrl() != null && dto.getImageUrl().startsWith("http")
                        ? dto.getImageUrl().substring(dto.getImageUrl().lastIndexOf("/") + 1)
                        : dto.getImageUrl()
        );
        product.setMrp(dto.getMrp());
        product.setDiscount(dto.getDiscount());
        product.setDescription(dto.getDescription());
        product.setAbout(dto.getAbout());
        product.setStock(dto.getStock());

        // ✅ Update attributes: delete old and save new
        productAttributeRepo.deleteByProduct_ProductId(productId);
        if (dto.getAttributes() != null) {
            for (ProductAttributeDto attrDto : dto.getAttributes()) {
                ProductAttribute attr = new ProductAttribute();
                attr.setProduct(product);
                attr.setAttributeName(attrDto.getAttributeName());
                attr.setAttributeValue(attrDto.getAttributeValue());
                productAttributeRepo.save(attr);
            }
        }

        Set<Tag> managedTags = manageTags(dto.getTags());
        product.setTags(managedTags);

        Product updatedProduct = productRepo.save(product);
        return convertToDto(updatedProduct);
    }

    private void validateProduct(ProductDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (dto.getMrp() == null || dto.getMrp() < 0) {
            throw new IllegalArgumentException("MRP must be >= 0");
        }
        if (dto.getDiscount() < 0) {
            throw new IllegalArgumentException("Discount must be >= 0");
        }
        if (dto.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (productRepo.existsByName(dto.getName().trim())) {
            throw new IllegalArgumentException("Product with this name already exists");
        }
    }

    @Transactional
    public List<ProductDto> getAllProducts() {
        return productRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto getProductById(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with ID " + productId + " not found"));
        return convertToDto(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepo.existsById(productId)) {
            throw new RuntimeException("Product with ID " + productId + " not found");
        }

        // Delete dependent records first
        wishlistRepo.deleteByProduct_ProductId(productId);
        promoCodeRepo.deleteByProduct_ProductId(productId);
        productReviewRepo.deleteByProduct_ProductId(productId);

        // Delete product
        productRepo.deleteById(productId);
    }

    @Transactional
    public ProductDto updateStock(Long productId, int newStock) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with ID " + productId + " not found"));

        product.setStock(newStock);
        Product updatedProduct = productRepo.save(product);
        return convertToDto(updatedProduct);
    }

    private ProductDto convertToDto(Product product) {
        String imageFile = (product.getImageFilename() != null && !product.getImageFilename().isEmpty())
                ? product.getImageFilename()
                : null;

        double avgRating = productReviewRepo.findByProduct_ProductId(product.getProductId())
                .stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);

        ProductDto dto = new ProductDto(
                product.getProductId(),
                product.getName(),
                imageFile,
                product.getMrp(),
                product.getDiscount(),
                product.getDescription(),
                product.getAbout(),
                product.getTags() != null ? product.getTags() : Set.of(),
                product.getStock(),
                avgRating
        );


        // ✅ Add attributes
        List<ProductAttributeDto> attrDtos = productAttributeRepo.findByProduct_ProductId(product.getProductId())
                .stream()
                .map(attr -> new ProductAttributeDto(attr.getAttributeName(), attr.getAttributeValue()))
                .collect(Collectors.toList());
        dto.setAttributes(attrDtos);

        return dto;  // <-- return dto here after setting attributes
    }


    private Set<Tag> manageTags(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) return new HashSet<>();

        Set<Tag> managedTags = new HashSet<>();
        for (Tag tag : tags) {
            Optional<Tag> existingTag = tagRepository.findByName(tag.getName());
            if (existingTag.isPresent()) {
                managedTags.add(existingTag.get());
            } else {
                managedTags.add(tagRepository.save(tag));
            }
        }
        return managedTags;
    }
}
