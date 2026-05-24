package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.product_details_dtos.ProductDetailsRequestDTO;
import com.LoQueHay.project.dto.product_details_dtos.ProductDetailsResponseDTO;
import com.LoQueHay.project.mappers.ProductDetailsMapper;
import com.LoQueHay.project.model.ProductDetails;
import com.LoQueHay.project.service.ProductDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/{productId}/details")
public class ProductDetailsController {

    private final ProductDetailsService service;

    public ProductDetailsController(ProductDetailsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductDetailsResponseDTO> create(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDetailsRequestDTO dto
    ) {
        ProductDetails created = service.create(productId, dto);
        return ResponseEntity.status(201).body(ProductDetailsMapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<ProductDetailsResponseDTO> get(@PathVariable Long productId) {
        ProductDetails details = service.getByProduct(productId);
        return ResponseEntity.ok(ProductDetailsMapper.toDTO(details));
    }

    @PutMapping
    public ResponseEntity<ProductDetailsResponseDTO> update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductDetailsRequestDTO dto
    ) {
        ProductDetails updated = service.update(productId, dto);
        return ResponseEntity.ok(ProductDetailsMapper.toDTO(updated));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
