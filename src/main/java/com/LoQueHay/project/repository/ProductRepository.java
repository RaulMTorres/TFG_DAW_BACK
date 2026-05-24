package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.Category;
import com.LoQueHay.project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByOwnerId(Long ownerId);

    Long countByOwnerIdAndCategory(Long ownerId, Category category);

    Optional<Product> findByIdAndOwnerId(Long id, Long ownerId);

    // Verifica si existe un producto con el SKU dado
    boolean existsBySku(String sku);

    // Verifica si existe un producto con el barcode dado
    boolean existsByBarcode(String barcode);

    boolean existsByOwnerIdAndCategoryId(Long ownerId, Long categoryId);


}