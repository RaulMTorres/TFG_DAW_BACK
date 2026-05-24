package com.LoQueHay.project.dto.dashboard_dtos;

public class CategoryStockDTO {

    private String categoryName;
    private long count;

    public CategoryStockDTO() {}
    public CategoryStockDTO(String categoryName, long count) {
        this.categoryName = categoryName;
        this.count = count;
    }

    // Getters y Setters
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
