package com.LoQueHay.project.dto.report_dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class InventorySummaryRow {

    private String productName;
    private String categoryName;
    private String warehouseName;
    private Integer quantity;
    private Double unitCost;
    private Double totalValue;

    public InventorySummaryRow() {
    }

    public InventorySummaryRow(String productName, String categoryName, String warehouseName, Integer quantity, Double unitCost, Double totalValue) {
        this.productName = productName;
        this.categoryName = categoryName;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalValue = totalValue;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }
}
