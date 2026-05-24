package com.LoQueHay.project.mappers;

import com.LoQueHay.project.dto.product_details_dtos.ProductDetailsResponseDTO;
import com.LoQueHay.project.model.ProductDetails;

public class ProductDetailsMapper {

    public static ProductDetailsResponseDTO toDTO(ProductDetails details) {
        ProductDetailsResponseDTO dto = new ProductDetailsResponseDTO();
        dto.setId(details.getId());

        dto.setWeight(details.getWeight());
        dto.setWeightUnit(details.getWeightUnit());

        dto.setLength(details.getLength());
        dto.setWidth(details.getWidth());
        dto.setDimensionUnit(details.getDimensionUnit());

        return dto;
    }
}
