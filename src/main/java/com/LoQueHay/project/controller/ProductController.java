package com.LoQueHay.project.controller;


import com.LoQueHay.project.dto.product_dtos.ProductRequestDTO;
import com.LoQueHay.project.dto.product_dtos.ProductResponseDTO;
import com.LoQueHay.project.mappers.ProductMapper;
import com.LoQueHay.project.model.Product;
import com.LoQueHay.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll(){
        return ResponseEntity.ok(service.getAll().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> productsPage = service.getProducts(search, sku, categoryId, page, size);

        // Mapeamos la Page<Product> a Page<ProductResponseDTO>
        Page<ProductResponseDTO> dtoPage = productsPage.map(ProductMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(ProductMapper.toDTO(service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto){
        ProductResponseDTO created =ProductMapper.toDTO(service.create(dto));
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,@Valid @RequestBody ProductRequestDTO dto){
        ProductResponseDTO updated = ProductMapper.toDTO(service.update(id,dto));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> ids) {
        service.deleteMultiple(ids);
        return ResponseEntity.noContent().build();
    }

}
