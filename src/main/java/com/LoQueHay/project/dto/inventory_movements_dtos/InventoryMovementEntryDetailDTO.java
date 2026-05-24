package com.LoQueHay.project.dto.inventory_movements_dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class InventoryMovementEntryDetailDTO {
    @NotNull(message = "productId es obligatorio")
    private Long productId;

    @NotNull(message = "quantity es obligatorio")
    @Positive(message = "quantity debe ser mayor que 0")
    private Integer quantity;

    @NotNull(message = "unitCost es obligatorio")
    @PositiveOrZero(message = "unitCost debe ser mayor o igual a 0")
    private Double unitCost;

    private String lotNumber;
    private String expirationDate; // ISO String, opcional

    public @NotNull(message = "productId es obligatorio") Long getProductId() {
        return productId;
    }

    public void setProductId(@NotNull(message = "productId es obligatorio") Long productId) {
        this.productId = productId;
    }

    public @NotNull(message = "quantity es obligatorio") @Positive(message = "quantity debe ser mayor que 0") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "quantity es obligatorio") @Positive(message = "quantity debe ser mayor que 0") Integer quantity) {
        this.quantity = quantity;
    }

    public @NotNull(message = "unitCost es obligatorio") @PositiveOrZero(message = "unitCost debe ser mayor o igual a 0") Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(@NotNull(message = "unitCost es obligatorio") @PositiveOrZero(message = "unitCost debe ser mayor o igual a 0") Double unitCost) {
        this.unitCost = unitCost;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
