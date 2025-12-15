package com.opsmonsters.quick_bite.services;

import com.opsmonsters.quick_bite.dto.TagDto;
import com.opsmonsters.quick_bite.models.Product;
import com.opsmonsters.quick_bite.models.Tag;
import com.opsmonsters.quick_bite.repositories.ProductRepo;
import com.opsmonsters.quick_bite.repositories.TagRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private ProductRepo productRepo;


    public TagDto createTag(TagDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());

        Tag savedTag = tagRepo.save(tag);
        dto.setTagId(savedTag.getTagId());
        return dto;
    }


    public List<TagDto> getAllTags() {
        return tagRepo.findAll()
                .stream()
                .map(tag -> {
                    TagDto dto = new TagDto();
                    dto.setTagId(tag.getTagId());
                    dto.setName(tag.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public TagDto addTagsToProduct(Long productId, Set<Long> tagIds) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Set<Tag> tags = tagRepo.findAllById(tagIds).stream().collect(Collectors.toSet());

        if (tags.isEmpty()) {
            throw new RuntimeException("No valid tags found. Ensure the tags exist before adding.");
        }


        product.getTags().clear();


        for (Tag tag : tags) {
            product.addTag(tag);
        }

        productRepo.save(product);
        return new TagDto();
    }


    public void deleteTag(Long tagId) {
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));


        for (Product product : tag.getProducts()) {
            product.removeTag(tag);
        }

        tagRepo.delete(tag);
    }
}