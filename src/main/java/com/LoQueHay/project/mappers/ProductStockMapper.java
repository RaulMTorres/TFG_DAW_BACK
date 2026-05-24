package com.LoQueHay.project.mappers;


import com.LoQueHay.project.dto.product_stock_dtos.ProductStockRequestDTO;
import com.LoQueHay.project.dto.product_stock_dtos.ProductStockResponseDTO;
import com.LoQueHay.project.model.Product;
import com.LoQueHay.project.model.ProductStock;
import com.LoQueHay.project.model.Warehouse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductStockMapper {

    public static ProductStock toEntity(ProductStockRequestDTO dto, Product product, Warehouse warehouse){
        ProductStock stock = new ProductStock();
        stock.setQuantity(dto.getQuantity());
        stock.setLotNumber((dto.getLotNumber() != null && !dto.getLotNumber().isEmpty())
                ? dto.getLotNumber()
                : "AUTO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        stock.setExpirationDate(dto.getExpirationDate());
        stock.setProduct(product);
        stock.setWarehouse(warehouse);
        stock.setUnitCost(dto.getUnitCost());
        return stock;
    }

    public static ProductStockResponseDTO toDTO(ProductStock stock){
        ProductStockResponseDTO dto = new ProductStockResponseDTO();
        dto.setId(stock.getId());
        dto.setQuantity(stock.getQuantity());
        dto.setLotNumber(stock.getLotNumber());
        dto.setExpirationDate(stock.getExpirationDate());
        dto.setWarehouseId(stock.getWarehouse().getId());
        dto.setWarehouseName(stock.getWarehouse().getName());
        dto.setUnitCost(stock.getUnitCost());
        return dto;
    }
}
