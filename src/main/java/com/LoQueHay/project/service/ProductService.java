package com.LoQueHay.project.service;

import com.LoQueHay.project.Specification.ProductSpecifications;
import com.LoQueHay.project.dto.product_dtos.ProductRequestDTO;
import com.LoQueHay.project.exception.BadRequestException;
import com.LoQueHay.project.exception.DuplicateResourceException;
import com.LoQueHay.project.exception.ResourceNotFoundException;
import com.LoQueHay.project.mappers.ProductMapper;
import com.LoQueHay.project.model.*;
import com.LoQueHay.project.repository.*;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service

public class ProductService {

    private final ProductRepository productRepository;
    private final AuthUtils authUtils;
    private final CategoryService categoryService;
    private final ProductStockRepository productStockRepository;
    private final InventoryMovementDetailRepository inventoryMovementDetailRepository;

    public ProductService(ProductRepository productRepository, AuthUtils authUtils, CategoryService categoryService, ProductStockRepository productStockRepository, InventoryMovementDetailRepository inventoryMovementDetailRepository) {
        this.productRepository = productRepository;
        this.authUtils = authUtils;
        this.categoryService = categoryService;
        this.productStockRepository = productStockRepository;
        this.inventoryMovementDetailRepository = inventoryMovementDetailRepository;
    }

    public List<Product> getAll(){
        MyUserEntity user = authUtils.getCurrentUser();
        return productRepository.findByOwnerId(user.getOwner().getId());
    }

    public Page<Product> getProducts(
            String search,
            String sku,
            Long categoryId,
            int page,
            int size
    ) {
        Long ownerId = authUtils.getCurrentUser().getOwner().getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Specification<Product> spec = ProductSpecifications.belongsToOwner(ownerId);

        // Filtro por nombre
        if (search != null && !search.isEmpty()) {
            spec = (spec == null) ? ProductSpecifications.nameContains(search) : spec.and(ProductSpecifications.nameContains(search));
        }

        // Filtro por SKU
        if (sku != null && !sku.isEmpty()) {
            spec = (spec == null) ? ProductSpecifications.skuContains(sku)
                    : spec.and(ProductSpecifications.skuContains(sku));
        }

        // Filtro por categoría
        if (categoryId != null) {
            spec = (spec == null) ? ProductSpecifications.categoryId(categoryId) : spec.and(ProductSpecifications.categoryId(categoryId));
        }

        return (spec == null)
                ? productRepository.findAll(pageable)
                : productRepository.findAll(spec, pageable);
    }

    public Product getById(Long id){
        MyUserEntity user = authUtils.getCurrentUser();

        return productRepository.findByIdAndOwnerId(id, user.getOwner().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for this owner"));
    }

    public Long countByCategory(Category category){
        MyUserEntity user = authUtils.getCurrentUser();
        Long total = productRepository.countByOwnerIdAndCategory(user.getOwner().getId(), category);
        return total;
    }

    @Transactional
    public Product create(ProductRequestDTO dto){

        MyUserEntity user = authUtils.getCurrentUser();
        // Validar SKU
        if(productRepository.existsBySku(dto.getSku())){
            throw new DuplicateResourceException("SKU ya existe");
        }

        // Validar Barcode
        if(productRepository.existsByBarcode(dto.getBarcode())){
            throw new DuplicateResourceException("Barcode ya existe");
        }
        Category category = categoryService.getById(dto.getCategoryId());


        Product product = ProductMapper.toEntity(dto, category, user);

        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequestDTO dto) {
        MyUserEntity user = authUtils.getCurrentUser();

        Product existing = this.getById(id);

        // Validar SKU solo si se cambió y existe otro con ese SKU
        if (!existing.getSku().equals(dto.getSku()) && productRepository.existsBySku(dto.getSku())) {
            throw new DuplicateResourceException("SKU ya existe");
        }

        // Validar Barcode solo si se cambió y existe otro con ese Barcode
        if (!existing.getBarcode().equals(dto.getBarcode()) && productRepository.existsByBarcode(dto.getBarcode())) {
            throw new DuplicateResourceException("Barcode ya existe");
        }

        // Actualizar campos
        existing.setSku(dto.getSku());
        existing.setBarcode(dto.getBarcode());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setHasExpirationDate(dto.getHasExpirationDate());

        Category category = categoryService.getById(dto.getCategoryId());
        existing.setCategory(category);


        return productRepository.save(existing);
    }


    public void delete(Long id){
        this.getById(id);
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteMultiple(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;

        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        for (Long id : ids) {
            if (id == null) continue;

            // 1) Validar que exista y sea del owner (multitenant)
            Product product = productRepository.findByIdAndOwnerId(id, ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for this owner: " + id));

            // 2) Bloquear si tiene stock disponible
            boolean hasStock = productStockRepository
                    .existsByProductIdAndWarehouseOwnerIdAndQuantityGreaterThan(product.getId(), ownerId, 0);

            if (hasStock) {
                throw new BadRequestException("El producto '" + product.getName() + "' no se puede eliminar porque tiene stock.");
            }

            // 3) Bloquear si tiene movimientos (histórico)
            boolean hasMovements = inventoryMovementDetailRepository
                    .existsByProductIdAndMovementOwnerId(product.getId(), ownerId);

            if (hasMovements) {
                throw new BadRequestException("El producto '" + product.getName() + "' no se puede eliminar porque tiene movimientos asociados.");
            }

            // 4) Eliminar (si usas orphanRemoval en details/stock etc, ok)
            productRepository.delete(product);
        }
    }

}
