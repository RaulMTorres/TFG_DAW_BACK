package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.dashboard_dtos.*;
import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.MovementType;
import com.LoQueHay.project.repository.InventoryMovementRepository;
import com.LoQueHay.project.service.InventoryMovementService;
import com.LoQueHay.project.service.ProductStockService;
import com.LoQueHay.project.service.reports.ReportService;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final InventoryMovementService inventoryMovementService;

    public ReportController(ReportService reportService, InventoryMovementService inventoryMovementService) {
        this.reportService = reportService;
        this.inventoryMovementService = inventoryMovementService;
    }


    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateReport(@RequestBody ReportRequestDTO request) {
        try {
            byte[] file = reportService.generateReport(request);

            // Determinar tipo MIME según el formato
            String contentType = switch (request.getFormat().toUpperCase()) {
                case "PDF" -> MediaType.APPLICATION_PDF_VALUE;
                case "EXCEL" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                case "CSV" -> "text/csv";
                default -> "application/octet-stream";
            };

            // Nombre del archivo
            String filename = request.getReportType().toLowerCase() + "." + request.getFormat().toLowerCase();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(file);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generando reporte: " + e.getMessage()).getBytes());
        }
    }


    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        DashboardSummaryDTO summary = reportService.getSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/dashboard/stock-by-category")
    public ResponseEntity<List<CategoryStockDTO>> getCategoryStock() {
        return ResponseEntity.ok(reportService.getCategoryStock());
    }

    @GetMapping("/dashboard/stock-value-by-warehouse")
    public ResponseEntity<List<StockByWarehouseDTO>> getStockByWarehouse() {
        return ResponseEntity.ok(reportService.getStockByWarehouse());
    }

    @GetMapping("/dashboard/products-expiring")
    public ResponseEntity<List<ProductsExpiringDTO>> getExpiringProductsByPeriod() {
        return ResponseEntity.ok(reportService.getExpiringProductsByPeriod());
    }

    @GetMapping("/dashboard/monthly-sales-purchases")
    public ResponseEntity<MonthlySalesPurchasesDTO> getMonthlySalesAndPurchases() {
        return ResponseEntity.ok(inventoryMovementService.getMonthlySalesAndPurchases());
    }
}

