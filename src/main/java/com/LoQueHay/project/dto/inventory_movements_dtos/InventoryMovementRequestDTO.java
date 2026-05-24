package com.LoQueHay.project.dto.inventory_movements_dtos;

import com.LoQueHay.project.model.MovementType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class InventoryMovementRequestDTO {
    @NotNull(message = "El movementType es obligatorio")
    private MovementType movementType;

    @NotNull(message = "warehouseId es obligatorio")
    private Long warehouseId;

    private String referenceDocument;
    private String note;

    // Para entradas
    @Valid
    private List<InventoryMovementEntryDetailDTO> entryDetails;

    // Para salidas
    @Valid
    private List<InventoryMovementExitDetailDTO> exitDetails;


    public List<InventoryMovementEntryDetailDTO> getEntryDetails() {
        return entryDetails;
    }

    public void setEntryDetails(List<InventoryMovementEntryDetailDTO> entryDetails) {
        this.entryDetails = entryDetails;
    }

    public List<InventoryMovementExitDetailDTO> getExitDetails() {
        return exitDetails;
    }

    public void setExitDetails(List<InventoryMovementExitDetailDTO> exitDetails) {
        this.exitDetails = exitDetails;
    }

    public @NotNull(message = "El movementType es obligatorio") MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(@NotNull(message = "El movementType es obligatorio") MovementType movementType) {
        this.movementType = movementType;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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


}
