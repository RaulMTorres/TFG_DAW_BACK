package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.ProductStock;
import com.LoQueHay.project.repository.ProductStockRepository;
import com.LoQueHay.project.service.reports.pdf.PdfReportBuilder;
import com.LoQueHay.project.service.reports.specifications.ProductStockSpecs;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StockValuationReportGenerator implements ReportGenerator {

    private final ProductStockRepository productStockRepository;

    public StockValuationReportGenerator(ProductStockRepository productStockRepository) {
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

        List<Map<String, Object>> rows = stocks.stream()
                .map(stock -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("Producto", stock.getProduct().getName());
                    row.put("SKU", stock.getProduct().getSku() != null ? stock.getProduct().getSku() : "—");
                    row.put("Categoría", stock.getProduct().getCategory().getName());
                    row.put("Almacén", stock.getWarehouse().getName());
                    row.put("Lote", stock.getLotNumber());
                    row.put("Cantidad", stock.getQuantity());
                    row.put("Costo Unitario ($)", stock.getUnitCost());
                    row.put("Valor Total ($)", stock.getQuantity() * stock.getUnitCost());
                    if (stock.getExpirationDate() != null) {
                        row.put("Vencimiento", stock.getExpirationDate().toString());
                    } else {
                        row.put("Vencimiento", "—");
                    }
                    return row;
                })
                .collect(Collectors.toList());

        List<String> columns = List.of(
                "Producto", "SKU", "Categoría", "Almacén", "Lote",
                "Cantidad", "Costo Unitario ($)", "Valor Total ($)", "Vencimiento"
        );

        return PdfReportBuilder.buildReport("Valoración de Stock por Lote", columns, rows);
    }
}
