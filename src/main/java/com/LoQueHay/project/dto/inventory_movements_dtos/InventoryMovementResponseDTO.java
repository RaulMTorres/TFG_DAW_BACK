package com.LoQueHay.project.dto.inventory_movements_dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InventoryMovementResponseDTO {

    private Long id;
    private String movementType;
    private String referenceDocument;
    private String note;
    private String warehouseName;
    private String ownerName;
    private String createdByName;
    private LocalDateTime createdAt;

    private List<InventoryMovementDetailResponseDTO> details;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(String referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<InventoryMovementDetailResponseDTO> getDetails() {
        return details;
    }

    public void setDetails(List<InventoryMovementDetailResponseDTO> details) {
        this.details = details;
    }
}
