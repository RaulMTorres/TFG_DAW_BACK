package com.LoQueHay.project.mappers;


import com.LoQueHay.project.dto.category_dtos.CategoryResponseDTO;
import com.LoQueHay.project.model.Category;
public class CategoryMapper {

    public static CategoryResponseDTO toDTO(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setOwnerId(category.getOwner().getId());
        dto.setCreatedById(category.getCreatedBy().getId());
        dto.setCreatedAt(category.getCreatedAt());

        return dto;
    }

    public static CategoryResponseDTO toDTOwithTotalProducts(Category category,Long totalProducts) {
        CategoryResponseDTO dto = toDTO(category);
        dto.setTotalProducts(totalProducts);
        return dto;
    }
}
