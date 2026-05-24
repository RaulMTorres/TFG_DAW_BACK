package com.LoQueHay.project.dto.inventory_movements_dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class InventoryMovementExitDetailDTO {

    @NotNull(message = "productId es obligatorio")
    private Long productId;

    @NotNull(message = "quantity es obligatorio")
    @Positive(message = "quantity debe ser mayor que 0")
    private Integer quantity;

    @NotNull(message = "sellPriceUnit es obligatorio")
    @PositiveOrZero(message = "sellPriceUnit debe ser mayor o igual a 0")
    private Double sellPriceUnit;

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

    public @NotNull(message = "sellPriceUnit es obligatorio") @PositiveOrZero(message = "sellPriceUnit debe ser mayor o igual a 0") Double getSellPriceUnit() {
        return sellPriceUnit;
    }

    public void setSellPriceUnit(@NotNull(message = "sellPriceUnit es obligatorio") @PositiveOrZero(message = "sellPriceUnit debe ser mayor o igual a 0") Double sellPriceUnit) {
        this.sellPriceUnit = sellPriceUnit;
    }
}
