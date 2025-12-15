package com.opsmonsters.quick_bite.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Product> products = new HashSet<>();

    // ✅ No-args constructor (required by JPA)
    public Tag() {}

    // ✅ Constructor with parameters (for easier object creation)
    public Tag(String name) {
        this.name = name;
    }

    // ✅ Constructor with all fields (useful for DTO conversions)
    public Tag(Long tagId, String name) {
        this.tagId = tagId;
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
        product.getTags().add(this);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        product.getTags().remove(this);
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
