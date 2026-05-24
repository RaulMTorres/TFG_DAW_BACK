package com.LoQueHay.project.dto.product_dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class ProductRequestDTO {

    @NotBlank(message = "SKU no puede estar vacío")
    private String sku;
    @NotBlank(message = "Barcode no puede estar vacío")
    private String barcode;

    @NotBlank(message = "Nombre es obligatorio")
    private String name;
    private String description;
    private Boolean hasExpirationDate;

    @NotNull(message = "CategoryId es obligatorio")
    private Long categoryId;
    private Set<Long> tagIds;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public Boolean getHasExpirationDate() {
        return hasExpirationDate;
    }

    public void setHasExpirationDate(Boolean hasExpirationDate) {
        this.hasExpirationDate = hasExpirationDate;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }
}