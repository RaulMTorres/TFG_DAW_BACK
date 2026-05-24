package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.InventoryMovement;
import com.LoQueHay.project.model.MovementType;
import com.LoQueHay.project.repository.InventoryMovementRepository;
import com.LoQueHay.project.service.reports.pdf.PdfReportBuilder;
import com.LoQueHay.project.service.reports.specifications.InventoryMovementSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PurchaseReportGenerator implements ReportGenerator {

    private final InventoryMovementRepository inventoryMovementRepository;

    public PurchaseReportGenerator(InventoryMovementRepository inventoryMovementRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Override
    public byte[] generate(ReportRequestDTO request) {

        Specification<InventoryMovement> spec = InventoryMovementSpecs.filterPurchases(
                request.getOwnerId(),
                request.getWarehouseId(),
                request.getCategoryId(),
                MovementType.IN,
                request.getDateFrom().atStartOfDay(),
                request.getDateTo().atTime(23, 59, 59)
        );

        List<InventoryMovement> purchases = inventoryMovementRepository.findAll(spec);

        List<Map<String, Object>> rows = purchases.stream()
                .flatMap(movement -> movement.getDetails().stream()
                        .map(detail -> {
                            double costo = detail.getUnitCost() != null ? detail.getUnitCost() : 0.0;
                            int cantidad = detail.getQuantity() != null ? detail.getQuantity() : 0;

                            Map<String, Object> row = new HashMap<>();
                            row.put("Fecha", movement.getCreatedAt().toLocalDate().toString());
                            row.put("Referencia", movement.getReferenceDocument() != null ? movement.getReferenceDocument() : "—");
                            row.put("Almacén", movement.getWarehouse().getName());
                            row.put("Producto", detail.getProduct().getName());
                            row.put("Categoría", detail.getProduct().getCategory().getName());
                            row.put("Lote", detail.getLotNumber() != null ? detail.getLotNumber() : "—");
                            row.put("Cantidad", cantidad);
                            row.put("Costo Unitario ($)", costo);
                            row.put("Total Compra ($)", costo * cantidad);
                            row.put("Vencimiento", detail.getExpirationDate() != null
                                    ? detail.getExpirationDate().toString() : "—");
                            return row;
                        })
                )
                .sorted((a, b) -> String.valueOf(a.get("Fecha")).compareTo(String.valueOf(b.get("Fecha"))))
                .collect(Collectors.toList());

        List<String> columns = List.of(
                "Fecha", "Referencia", "Almacén", "Producto", "Categoría",
                "Lote", "Cantidad", "Costo Unitario ($)", "Total Compra ($)", "Vencimiento"
        );

        return PdfReportBuilder.buildReport("Reporte de Compras", columns, rows);
    }
}
