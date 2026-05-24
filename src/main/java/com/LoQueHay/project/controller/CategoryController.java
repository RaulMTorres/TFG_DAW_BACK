package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.category_dtos.CategoryRequestDTO;
import com.LoQueHay.project.dto.category_dtos.CategoryResponseDTO;
import com.LoQueHay.project.mappers.CategoryMapper;
import com.LoQueHay.project.model.Category;
import com.LoQueHay.project.service.CategoryService;
import com.LoQueHay.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;
    private final ProductService productService;

    public CategoryController(CategoryService service, ProductService productService) {
        this.service = service;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO dto) {
        Category created = service.create(dto);
        return ResponseEntity.status(201).body(CategoryMapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAll() {
        List<CategoryResponseDTO> dtos = service.getAll().stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<CategoryResponseDTO>> getPagedCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Page<CategoryResponseDTO> categories = service.getCategories(page, size, search)
                .map(c -> CategoryMapper.toDTOwithTotalProducts(c,productService.countByCategory(c)));
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id){
        Category category = service.getById(id);
        Long x = productService.countByCategory(category);
        return ResponseEntity.ok(CategoryMapper.toDTO(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO dto){
        Category updated = service.update(id, dto);
        return ResponseEntity.ok(CategoryMapper.toDTO(updated));
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
