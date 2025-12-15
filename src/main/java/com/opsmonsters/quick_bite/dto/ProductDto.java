package com.opsmonsters.quick_bite.dto;

import com.opsmonsters.quick_bite.models.Tag;
import com.opsmonsters.quick_bite.services.ProductReviewService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private Long productId;
    private String name;
    private String imageUrl;   // will store only filename, getter will build full path
    private Double mrp;
    private Double discount;
    private String description;
    private String about;
    private Set<Tag> tags;
    private Integer stock;
    private Double averageRating;
    private LocalDateTime createdAt;
    private List<String> colors;
    private List<String> sizes;
    private List<String> imageUrls;

    private static final String BASE_IMAGE_URL = "http://13.61.26.222/uploads/";


    public ProductDto() {}

    public ProductDto(Long productId, String name, String imageUrl, Double mrp, Double discount,
                      String description, String about, Set<Tag> tags,
                      Integer stock, Double averageRating) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.mrp = mrp != null ? mrp : 0;
        this.discount = discount != null ? discount : 0;
        this.description = description;
        this.about = about;
        this.tags = tags;
        this.stock = stock != null ? stock : 0;
        this.averageRating = averageRating != null ? averageRating : 0;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Single imageUrl
    @JsonProperty("imageUrl")
    public String getImageUrl() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return BASE_IMAGE_URL + "no-image.png";
        }
        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }
        return BASE_IMAGE_URL + imageUrl; // filename -> full url
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Multiple imageUrls
    @JsonProperty("imageUrls")
    public List<String> getImageUrls() {
        if (imageUrls == null) return List.of();
        return imageUrls.stream()
                .map(img -> (img.startsWith("http") ? img : BASE_IMAGE_URL + img))
                .collect(Collectors.toList());
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }

    private List<ProductAttributeDto> attributes;
    public List<ProductAttributeDto> getAttributes() { return attributes; }
    public void setAttributes(List<ProductAttributeDto> attributes) { this.attributes = attributes; }

    public Double getMrp() { return mrp; }
    public void setMrp(Double mrp) { this.mrp = mrp; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    @JsonProperty
    public Double getPrice() {
        return (mrp != null && discount != null) ? Math.max(mrp - discount, 0) : 0;
    }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public void setAverageRatingFromService(ProductReviewService reviewService) {
        this.averageRating = reviewService.getAverageRating(this.productId);
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
