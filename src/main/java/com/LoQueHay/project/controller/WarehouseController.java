package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.category_dtos.CategoryResponseDTO;
import com.LoQueHay.project.dto.warehouse_dtos.WarehouseRequestDTO;
import com.LoQueHay.project.dto.warehouse_dtos.WarehouseResponseDTO;
import com.LoQueHay.project.mappers.CategoryMapper;
import com.LoQueHay.project.mappers.WarehouseMapper;
import com.LoQueHay.project.model.Warehouse;
import com.LoQueHay.project.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> create(@Valid @RequestBody WarehouseRequestDTO dto) {
        Warehouse created = service.create(dto);
        return ResponseEntity.status(201).body(WarehouseMapper.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAll() {
        List<WarehouseResponseDTO> dtos = service.getAll().stream()
                .map(WarehouseMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<WarehouseResponseDTO>> getWhareHouse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Page<WarehouseResponseDTO> wharehouses = service.getWharehouses(page, size, search)
                .map(WarehouseMapper::toDTO);
        return ResponseEntity.ok(wharehouses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getById(@PathVariable Long id) {
        Warehouse warehouse = service.getById(id);
        return ResponseEntity.ok(WarehouseMapper.toDTO(warehouse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> update(@PathVariable Long id,@Valid @RequestBody WarehouseRequestDTO dto) {
        Warehouse updated = service.update(id, dto);
        return ResponseEntity.ok(WarehouseMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> ids) {
        service.deleteMultiple(ids);
        return ResponseEntity.noContent().build();
    }
}
