package com.LoQueHay.project.mappers;

import com.LoQueHay.project.dto.inventory_movements_dtos.InventoryMovementDetailResponseDTO;
import com.LoQueHay.project.dto.inventory_movements_dtos.InventoryMovementResponseDTO;
import com.LoQueHay.project.model.InventoryMovement;
import com.LoQueHay.project.model.InventoryMovementDetail;
import com.LoQueHay.project.model.MovementType;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryMovementMapper {

    public static InventoryMovementResponseDTO toDTO(InventoryMovement movement) {
        InventoryMovementResponseDTO dto = new InventoryMovementResponseDTO();
        dto.setId(movement.getId());
        dto.setMovementType(movement.getMovementType().name());
        dto.setReferenceDocument(movement.getReferenceDocument());
        dto.setNote(movement.getNote());
        dto.setWarehouseName(movement.getWarehouse().getName());
        dto.setOwnerName(movement.getOwner().getUsername());
        dto.setCreatedByName(movement.getCreatedBy().getUsername());
        dto.setCreatedAt(movement.getCreatedAt());

        if (movement.getMovementType() == MovementType.IN) {
            // Detalles para entradas
            List<InventoryMovementDetailResponseDTO> details = movement.getDetails().stream()
                    .map(d -> {
                        InventoryMovementDetailResponseDTO detailDTO = new InventoryMovementDetailResponseDTO();
                        detailDTO.setProductId(d.getProduct().getId());
                        detailDTO.setProductName(d.getProduct().getName());
                        detailDTO.setQuantity(d.getQuantity());
                        detailDTO.setUnitCost(d.getUnitCost());
                        detailDTO.setLotNumber(d.getLotNumber());
                        detailDTO.setExpirationDate(d.getExpirationDate() != null ? d.getExpirationDate().toString() : null);
                        // Aquí puedes agregar más campos específicos de entradas si los hubiera
                        return detailDTO;
                    })
                    .collect(Collectors.toList());

            dto.setDetails(details);
        } else if (movement.getMovementType() == MovementType.OUT) {
            // Detalles para salidas
            List<InventoryMovementDetailResponseDTO> details = movement.getDetails().stream()
                    .map(d -> {
                        InventoryMovementDetailResponseDTO detailDTO = new InventoryMovementDetailResponseDTO();
                        detailDTO.setProductId(d.getProduct().getId());
                        detailDTO.setProductName(d.getProduct().getName());
                        detailDTO.setQuantity(d.getQuantity());
                        detailDTO.setSellPriceUnit(d.getSellPriceUnit());

                        return detailDTO;
                    })
                    .collect(Collectors.toList());

            dto.setDetails(details);
        }

        return dto;
    }
}
