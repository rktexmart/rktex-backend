package com.opsmonsters.quick_bite.dto;

public class TagDto {
    private Long tagId;
    private String name;

    // ✅ Add a No-Argument Constructor (Required by Jackson for Serialization)
    public TagDto() {
    }

    // ✅ Add a Constructor with Arguments to Fix Your Error
    public TagDto(Long tagId, String name) {
        this.tagId = tagId;
        this.name = name;
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
