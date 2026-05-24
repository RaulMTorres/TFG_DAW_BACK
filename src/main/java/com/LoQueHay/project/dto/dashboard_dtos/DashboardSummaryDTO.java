package com.LoQueHay.project.dto.dashboard_dtos;

public class DashboardSummaryDTO {

    private long totalProductsInStock;
    private double totalInventoryValue;
    private double salesThisMonth;
    private double purchasesThisMonth;

    public long getTotalProductsInStock() {
        return totalProductsInStock;
    }

    public void setTotalProductsInStock(long totalProductsInStock) {
        this.totalProductsInStock = totalProductsInStock;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(double totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public double getSalesThisMonth() {
        return salesThisMonth;
    }

    public void setSalesThisMonth(double salesThisMonth) {
        this.salesThisMonth = salesThisMonth;
    }

    public double getPurchasesThisMonth() {
        return purchasesThisMonth;
    }

    public void setPurchasesThisMonth(double purchasesThisMonth) {
        this.purchasesThisMonth = purchasesThisMonth;
    }
}