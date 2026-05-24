package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.inventory_movements_dtos.InventoryMovementRequestDTO;
import com.LoQueHay.project.dto.inventory_movements_dtos.InventoryMovementResponseDTO;
import com.LoQueHay.project.dto.product_dtos.ProductResponseDTO;
import com.LoQueHay.project.mappers.InventoryMovementMapper;
import com.LoQueHay.project.mappers.ProductMapper;
import com.LoQueHay.project.model.InventoryMovement;
import com.LoQueHay.project.model.Product;
import com.LoQueHay.project.service.InventoryMovementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-movements")
public class InventoryMovementController {

    private final InventoryMovementService service;

    public InventoryMovementController(InventoryMovementService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InventoryMovementResponseDTO> create(@Valid @RequestBody InventoryMovementRequestDTO dto){
        InventoryMovement movement = service.createMovement(dto);
        return ResponseEntity.status(201).body(InventoryMovementMapper.toDTO(movement));
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<InventoryMovementResponseDTO>> getPagedByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String movementType,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Double unitCost
    ) {
        Page<InventoryMovement> movements = service.getPagedMovementsByProduct(
                productId, reference, movementType, warehouseId, quantity, unitCost, page, size
        );

        Page<InventoryMovementResponseDTO> dtoPage = movements.map(InventoryMovementMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }



    @GetMapping("/paged")
    public ResponseEntity<Page<InventoryMovementResponseDTO>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String movementType,
            @RequestParam(required = false) Long warehouseId
    ) {
        Page<InventoryMovement> movements  = service.getPagedMovements(reference, movementType, warehouseId, page, size);

        Page<InventoryMovementResponseDTO> dtoPage = movements.map(InventoryMovementMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(InventoryMovementMapper.toDTO(service.getById(id)));
    }


    @PostMapping("/delete-multiple")
    public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> ids) {
        service.deleteMultiple(ids);
        return ResponseEntity.noContent().build();
    }
}
