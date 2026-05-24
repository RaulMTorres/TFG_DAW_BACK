package com.LoQueHay.project.service.reports.generators;

import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.InventoryMovement;
import com.LoQueHay.project.model.InventoryMovementDetail;
import com.LoQueHay.project.model.MovementType;
import com.LoQueHay.project.repository.InventoryMovementRepository;
import com.LoQueHay.project.service.reports.pdf.PdfReportBuilder;
import com.LoQueHay.project.service.reports.specifications.InventoryMovementSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reporte de Historial de Movimientos por Producto.
 * Muestra todas las entradas y salidas en el rango de fechas seleccionado,
 * con el valor monetario de cada movimiento.
 */
@Component
public class ProductMovementHistoryReportGenerator implements ReportGenerator {

    private final InventoryMovementRepository inventoryMovementRepository;

    public ProductMovementHistoryReportGenerator(InventoryMovementRepository inventoryMovementRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Override
    public byte[] generate(ReportRequestDTO request) {

        LocalDate from = request.getDateFrom() != null ? request.getDateFrom() : LocalDate.now().minusMonths(1);
        LocalDate to   = request.getDateTo()   != null ? request.getDateTo()   : LocalDate.now();

        // Obtener entradas
        Specification<InventoryMovement> specIn = InventoryMovementSpecs.filterPurchases(
                request.getOwnerId(), request.getWarehouseId(), request.getCategoryId(),
                request.getProductId(),
                MovementType.IN, from.atStartOfDay(), to.atTime(23, 59, 59)
        );

        // Obtener salidas
        Specification<InventoryMovement> specOut = InventoryMovementSpecs.filterPurchases(
                request.getOwnerId(), request.getWarehouseId(), request.getCategoryId(),
                request.getProductId(),
                MovementType.OUT, from.atStartOfDay(), to.atTime(23, 59, 59)
        );

        List<InventoryMovement> allMovements = Stream.concat(
                inventoryMovementRepository.findAll(specIn).stream(),
                inventoryMovementRepository.findAll(specOut).stream()
        ).sorted(Comparator.comparing(InventoryMovement::getCreatedAt))
         .collect(Collectors.toList());

        List<Map<String, Object>> rows = allMovements.stream()
                .flatMap(movement -> movement.getDetails().stream()
                        .map(detail -> {
                            boolean isIn = movement.getMovementType() == MovementType.IN;
                            double precio = isIn
                                    ? (detail.getUnitCost() != null ? detail.getUnitCost() : 0.0)
                                    : (detail.getSellPriceUnit() != null ? detail.getSellPriceUnit() : 0.0);
                            int cantidad = detail.getQuantity() != null ? detail.getQuantity() : 0;

                            Map<String, Object> row = new HashMap<>();
                            row.put("Fecha", movement.getCreatedAt().toLocalDate().toString());
                            row.put("Tipo", isIn ? "ENTRADA" : "SALIDA");
                            row.put("Referencia", movement.getReferenceDocument() != null ? movement.getReferenceDocument() : "—");
                            row.put("Almacén", movement.getWarehouse().getName());
                            row.put("Producto", detail.getProduct().getName());
                            row.put("Categoría", detail.getProduct().getCategory().getName());
                            row.put("Cantidad", cantidad);
                            row.put("Precio Unit. ($)", precio);
                            row.put("Valor ($)", precio * cantidad);
                            return row;
                        })
                )
                .collect(Collectors.toList());

        List<String> columns = List.of(
                "Fecha", "Tipo", "Referencia", "Almacén",
                "Producto", "Categoría", "Cantidad", "Precio Unit. ($)", "Valor ($)"
        );

        return PdfReportBuilder.buildReport(
                "Historial de Movimientos (" + from + " al " + to + ")",
                columns, rows
        );
    }
}
