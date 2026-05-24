package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.ProductStock;
import com.LoQueHay.project.repository.ProductStockRepository;
import com.LoQueHay.project.service.reports.pdf.PdfReportBuilder;
import com.LoQueHay.project.service.reports.specifications.ProductStockSpecs;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Reporte de Valor por Producto.
 * Por cada producto muestra: stock total (todas las bodegas), costo promedio ponderado y valor total en dinero.
 * Opcional: filtrar por almacén o categoría.
 */
@Component
public class ProductStockValueReportGenerator implements ReportGenerator {

    private final ProductStockRepository productStockRepository;

    public ProductStockValueReportGenerator(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    @Override
    public byte[] generate(ReportRequestDTO request) {

        List<ProductStock> stocks = productStockRepository.findAll(
                ProductStockSpecs.filterStocks(
                        request.getOwnerId(),
                        request.getWarehouseId(),
                        request.getCategoryId(),
                        request.getProductId(),
                        false,
                        null
                )
        );

        // Agrupar por producto consolidando todos los almacenes y lotes
        // Key = productId
        Map<Long, Map<String, Object>> grouped = new LinkedHashMap<>();

        for (ProductStock s : stocks) {
            Long productId = s.getProduct().getId();
            if (!grouped.containsKey(productId)) {
                Map<String, Object> row = new HashMap<>();
                row.put("_name", s.getProduct().getName());
                row.put("_sku", s.getProduct().getSku() != null ? s.getProduct().getSku() : "—");
                row.put("_category", s.getProduct().getCategory().getName());
                row.put("_qty", 0);
                row.put("_value", 0.0);
                row.put("_lots", 0);
                grouped.put(productId, row);
            }
            Map<String, Object> row = grouped.get(productId);
            int prevQty = ((Number) row.get("_qty")).intValue();
            double prevVal = ((Number) row.get("_value")).doubleValue();
            int prevLots = ((Number) row.get("_lots")).intValue();
            int qty = s.getQuantity();
            double cost = s.getUnitCost();
            row.put("_qty", prevQty + qty);
            row.put("_value", prevVal + (qty * cost));
            row.put("_lots", prevLots + 1);
        }

        List<Map<String, Object>> rows = grouped.values().stream()
                .map(row -> {
                    int qty = ((Number) row.get("_qty")).intValue();
                    double value = ((Number) row.get("_value")).doubleValue();
                    double avgCost = qty > 0 ? value / qty : 0.0;
                    int lots = ((Number) row.get("_lots")).intValue();

                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("Producto", row.get("_name"));
                    out.put("SKU", row.get("_sku"));
                    out.put("Categoría", row.get("_category"));
                    out.put("Lotes Activos", lots);
                    out.put("Unidades en Stock", qty);
                    out.put("Costo Prom. Unit. ($)", Math.round(avgCost * 100.0) / 100.0);
                    out.put("Valor Total ($)", Math.round(value * 100.0) / 100.0);
                    return out;
                })
                // Ordenar por valor total descendente (los más valiosos primero)
                .sorted((a, b) -> Double.compare(
                        ((Number) b.get("Valor Total ($)")).doubleValue(),
                        ((Number) a.get("Valor Total ($)")).doubleValue()
                ))
                .collect(Collectors.toList());

        List<String> columns = List.of(
                "Producto", "SKU", "Categoría", "Lotes Activos",
                "Unidades en Stock", "Costo Prom. Unit. ($)", "Valor Total ($)"
        );

        String title = request.getWarehouseId() != null
                ? "Valor de Stock por Producto (almacén filtrado)"
                : "Valor de Stock por Producto (todos los almacenes)";

        return PdfReportBuilder.buildReport(title, columns, rows);
    }
}
