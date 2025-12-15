// ProductAttributeDto.java
package com.opsmonsters.quick_bite.dto;

public class ProductAttributeDto {
    private String attributeName;
    private String attributeValue;

    public ProductAttributeDto() {}

    public ProductAttributeDto(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getAttributeName() { return attributeName; }
    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public String getAttributeValue() { return attributeValue; }
    public void setAttributeValue(String attributeValue) { this.attributeValue = attributeValue; }
}
