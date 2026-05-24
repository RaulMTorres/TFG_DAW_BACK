package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.product_stock_dtos.ProductStockRequestDTO;
import com.LoQueHay.project.dto.product_stock_dtos.ProductStockResponseDTO;
import com.LoQueHay.project.mappers.ProductStockMapper;
import com.LoQueHay.project.model.ProductStock;
import com.LoQueHay.project.service.ProductStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products/{productId}/stocks")
public class ProductStockController {

    private final ProductStockService service;

    public ProductStockController(ProductStockService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductStockResponseDTO> create(@PathVariable Long productId,
                                                          @RequestBody ProductStockRequestDTO dto){
        ProductStock created = service.create(productId, dto);
        return ResponseEntity.status(201).body(ProductStockMapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<ProductStockResponseDTO>> getAll(@PathVariable Long productId){
        List<ProductStockResponseDTO> list = service.getAllByProduct(productId)
                .stream()
                .map(ProductStockMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductStockResponseDTO> update(@PathVariable Long id,
                                                          @RequestBody ProductStockRequestDTO dto){
        ProductStock updated = service.update(id, dto);
        return ResponseEntity.ok(ProductStockMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
