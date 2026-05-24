package com.LoQueHay.project.dto.dashboard_dtos;

import java.util.List;

public class MonthlySalesPurchasesDTO {
    private List<String> months;      // ej: ["Enero", "Febrero", ...]
    private List<Double> sales;       // ventas por mes
    private List<Double> purchases;   // compras por mes

    public MonthlySalesPurchasesDTO() {}

    public MonthlySalesPurchasesDTO(List<String> months, List<Double> sales, List<Double> purchases) {
        this.months = months;
        this.sales = sales;
        this.purchases = purchases;
    }

    // Getters y Setters
    public List<String> getMonths() { return months; }
    public void setMonths(List<String> months) { this.months = months; }

    public List<Double> getSales() { return sales; }
    public void setSales(List<Double> sales) { this.sales = sales; }

    public List<Double> getPurchases() { return purchases; }
    public void setPurchases(List<Double> purchases) { this.purchases = purchases; }
}
