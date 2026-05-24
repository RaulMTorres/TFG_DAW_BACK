package com.LoQueHay.project.dto.dashboard_dtos;

public class ProductsExpiringDTO {

    private String daysRange; // ej: "0-7", "8-15", "16-30"
    private long count;

    public ProductsExpiringDTO() {}
    public ProductsExpiringDTO(String daysRange, long count) {
        this.daysRange = daysRange;
        this.count = count;
    }

    // Getters y Setters
    public String getDaysRange() { return daysRange; }
    public void setDaysRange(String daysRange) { this.daysRange = daysRange; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
