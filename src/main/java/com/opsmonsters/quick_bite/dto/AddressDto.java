package com.opsmonsters.quick_bite.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opsmonsters.quick_bite.models.Address;

import java.util.Objects;

public class AddressDto {

    @JsonProperty("addressId")
    private Long addressId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("street")
    private String street;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("country")
    private String country;

    @JsonProperty("default")
    private boolean isDefault;

    public AddressDto() {}

    public AddressDto(Long addressId, String fullName, String street, String city, String state,
                      String postalCode, String country, boolean isDefault) {
        this.addressId = addressId;
        this.fullName = fullName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = isDefault;
    }

    public AddressDto(Address address) {
        this.addressId = address.getAddressId();
        this.fullName = address.getFullName();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.isDefault = address.isDefault();
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AddressDto that = (AddressDto) obj;
        return isDefault == that.isDefault &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(street, that.street) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, street, city, state, postalCode, country, isDefault);
    }
}