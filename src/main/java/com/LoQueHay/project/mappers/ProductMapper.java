package com.LoQueHay.project.mappers;

import com.LoQueHay.project.dto.product_dtos.ProductRequestDTO;
import com.LoQueHay.project.dto.product_dtos.ProductResponseDTO;
import com.LoQueHay.project.model.Product;
import com.LoQueHay.project.model.Category;
import com.LoQueHay.project.model.MyUserEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapper {

    // DTO -> Entity
    public static Product toEntity(ProductRequestDTO dto, Category category, MyUserEntity user){
        Product product = new Product();
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setHasExpirationDate(dto.getHasExpirationDate());
        product.setCategory(category);
        product.setOwner(user);
        product.setCreatedBy(user);
        return product;
    }

    // Entity -> DTO
    public static ProductResponseDTO toDTO(Product product){
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setHasExpirationDate(product.getHasExpirationDate());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
