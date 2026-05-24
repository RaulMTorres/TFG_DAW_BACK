package com.LoQueHay.project.dto.product_details_dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductDetailsRequestDTO {

    @NotNull(message = "weight obligatorio")
    @Positive(message = "weight debe ser mayor que cero")
    private Double weight;

    @NotBlank(message = "weightUnit obligatorio")
    private String weightUnit; // kg, g, lb

    @NotNull(message = "length obligatorio")
    @Positive(message = "length debe ser mayor que cero")
    private Double length;

    @NotNull(message = "width obligatorio")
    @Positive(message = "width debe ser mayor que cero")
    private Double width;

    @NotBlank(message = "dimensionUnit obligatorio")
    private String dimensionUnit; // cm, m, in

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }

    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    public String getDimensionUnit() { return dimensionUnit; }
    public void setDimensionUnit(String dimensionUnit) { this.dimensionUnit = dimensionUnit; }
}
