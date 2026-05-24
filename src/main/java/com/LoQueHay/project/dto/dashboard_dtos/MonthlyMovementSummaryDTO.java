package com.LoQueHay.project.dto.dashboard_dtos;

import com.LoQueHay.project.model.MovementType;

import java.math.BigDecimal;

public class MonthlyMovementSummaryDTO {
    private Integer month;
    private Double totalValue;
    private MovementType movementType;

    public MonthlyMovementSummaryDTO(Integer month, Double totalValue, MovementType movementType) {
        this.month = month;
        this.totalValue = totalValue;
        this.movementType = movementType;
    }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }


    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }
}
