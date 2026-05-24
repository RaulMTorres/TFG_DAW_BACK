package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.ProductStock;
import com.LoQueHay.project.repository.ProductStockRepository;
import com.LoQueHay.project.service.reports.pdf.PdfReportBuilder;
import com.LoQueHay.project.service.reports.specifications.ProductStockSpecs;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExpirationReportGenerator implements ReportGenerator {

    private final ProductStockRepository productStockRepository;

    public ExpirationReportGenerator(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    @Override
    public byte[] generate(ReportRequestDTO request) {
        // Usar dateTo del request; si no viene, default a 90 días
        LocalDate expiryLimit = request.getDateTo() != null
                ? request.getDateTo()
                : LocalDate.now().plusDays(90);

        List<ProductStock> expiringStocks = productStockRepository.findAll(
                ProductStockSpecs.filterStocks(
                        request.getOwnerId(),
                        request.getWarehouseId(),
                        request.getCategoryId(),
                        null,
                        true,
                        expiryLimit
                )
        );

        List<Map<String, Object>> rows = expiringStocks.stream()
                .sorted((a, b) -> {
                    // Ordenar por fecha de vencimiento ascendente
                    if (a.getExpirationDate() == null) return 1;
                    if (b.getExpirationDate() == null) return -1;
                    return a.getExpirationDate().compareTo(b.getExpirationDate());
                })
                .map(stock -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("Producto", stock.getProduct().getName());
                    row.put("Categoría", stock.getProduct().getCategory().getName());
                    row.put("Almacén", stock.getWarehouse().getName());
                    row.put("Lote", stock.getLotNumber());
                    row.put("Cantidad", stock.getQuantity());
                    row.put("Costo Unitario ($)", stock.getUnitCost());
                    row.put("Valor en Riesgo ($)", stock.getQuantity() * stock.getUnitCost());
                    row.put("Fecha de Vencimiento", stock.getExpirationDate() != null
                            ? stock.getExpirationDate().toString() : "—");
                    long daysLeft = stock.getExpirationDate() != null
                            ? java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), stock.getExpirationDate())
                            : -1;
                    row.put("Días Restantes", daysLeft >= 0 ? daysLeft : "—");
                    return row;
                })
                .collect(Collectors.toList());

        List<String> columns = List.of(
                "Producto", "Categoría", "Almacén", "Lote", "Cantidad",
                "Costo Unitario ($)", "Valor en Riesgo ($)", "Fecha de Vencimiento", "Días Restantes"
        );

        String title = "Productos Próximos a Vencer (hasta " + expiryLimit + ")";
        return PdfReportBuilder.buildReport(title, columns, rows);
    }
}
