package com.LoQueHay.project.dto.dashboard_dtos;

public class StockByWarehouseDTO {
    private String warehouseName;
    private double totalValue;

    public StockByWarehouseDTO() {}
    public StockByWarehouseDTO(String warehouseName, double totalValue) {
        this.warehouseName = warehouseName;
        this.totalValue = totalValue;
    }

    // Getters y Setters
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }
}
