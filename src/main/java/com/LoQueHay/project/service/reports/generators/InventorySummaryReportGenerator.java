package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.ProductStock;
import com.LoQueHay.project.repository.ProductStockRepository;
import com.LoQueHay.project.service.reports.pdf.PdfReportBuilder;
import com.LoQueHay.project.service.reports.specifications.ProductStockSpecs;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InventorySummaryReportGenerator implements ReportGenerator {

    private final ProductStockRepository productStockRepository;

    public InventorySummaryReportGenerator(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    @Override
    public byte[] generate(ReportRequestDTO request) {

        // Obtener todos los lotes con stock > 0
        List<ProductStock> stocks = productStockRepository.findAll(
                ProductStockSpecs.filterStocks(
                        request.getOwnerId(),
                        request.getWarehouseId(),
                        request.getCategoryId(),
                        false
                )
        );

        // Agrupar por (Producto, Almacén) para consolidar lotes en una sola fila
        // Key = "productId_warehouseId"
        Map<String, Map<String, Object>> grouped = new LinkedHashMap<>();

        for (ProductStock s : stocks) {
            String key = s.getProduct().getId() + "_" + s.getWarehouse().getId();
            if (!grouped.containsKey(key)) {
                Map<String, Object> row = new HashMap<>();
                row.put("Producto", s.getProduct().getName());
                row.put("SKU", s.getProduct().getSku() != null ? s.getProduct().getSku() : "—");
                row.put("Categoría", s.getProduct().getCategory().getName());
                row.put("Almacén", s.getWarehouse().getName());
                row.put("_qty", 0);
                row.put("_value", 0.0);
                grouped.put(key, row);
            }
            Map<String, Object> row = grouped.get(key);
            int prevQty = ((Number) row.get("_qty")).intValue();
            double prevVal = ((Number) row.get("_value")).doubleValue();
            int qty = s.getQuantity();
            double cost = s.getUnitCost();
            row.put("_qty", prevQty + qty);
            row.put("_value", prevVal + (qty * cost));
        }

        // Convertir a filas con campos visibles
        List<Map<String, Object>> rows = grouped.values().stream()
                .map(row -> {
                    int qty = ((Number) row.get("_qty")).intValue();
                    double value = ((Number) row.get("_value")).doubleValue();
                    double avgCost = qty > 0 ? value / qty : 0.0;
                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("Producto", row.get("Producto"));
                    out.put("SKU", row.get("SKU"));
                    out.put("Categoría", row.get("Categoría"));
                    out.put("Almacén", row.get("Almacén"));
                    out.put("Cantidad Total", qty);
                    out.put("Costo Prom. ($)", Math.round(avgCost * 100.0) / 100.0);
                    out.put("Valor Total ($)", Math.round(value * 100.0) / 100.0);
                    return out;
                })
                .sorted(Comparator.comparing(r -> String.valueOf(r.get("Categoría")) + String.valueOf(r.get("Producto"))))
                .collect(Collectors.toList());

        List<String> columns = List.of(
                "Producto", "SKU", "Categoría", "Almacén",
                "Cantidad Total", "Costo Prom. ($)", "Valor Total ($)"
        );

        return PdfReportBuilder.buildReport("Resumen de Inventario", columns, rows);
    }
}
