package com.LoQueHay.project.dto.inventory_movements_dtos;

import lombok.Data;

@Data
public class InventoryMovementDetailResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitCost;
    private String lotNumber;
    private String expirationDate;
    private Double sellPriceUnit;

    public Double getSellPriceUnit() {
        return sellPriceUnit;
    }

    public void setSellPriceUnit(Double sellPriceUnit) {
        this.sellPriceUnit = sellPriceUnit;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
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
