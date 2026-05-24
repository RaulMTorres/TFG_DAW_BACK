package com.LoQueHay.project.service.reports;

import com.LoQueHay.project.service.reports.generators.*;
import org.springframework.stereotype.Component;

@Component
public class ReportFactory {

    private final InventorySummaryReportGenerator inventorySummary;
    private final SalesReportGenerator salesReport;
    private final PurchaseReportGenerator purchaseReport;
    private final StockValuationReportGenerator stockValuation;
    private final ExpirationReportGenerator expirationReport;
    private final ProductStockValueReportGenerator productStockValue;
    private final ProductMovementHistoryReportGenerator productMovementHistory;

    public ReportFactory(
            InventorySummaryReportGenerator inventorySummary,
            SalesReportGenerator salesReport,
            PurchaseReportGenerator purchaseReport,
            StockValuationReportGenerator stockValuation,
            ExpirationReportGenerator expirationReport,
            ProductStockValueReportGenerator productStockValue,
            ProductMovementHistoryReportGenerator productMovementHistory
    ) {
        this.inventorySummary = inventorySummary;
        this.salesReport = salesReport;
        this.purchaseReport = purchaseReport;
        this.stockValuation = stockValuation;
        this.expirationReport = expirationReport;
        this.productStockValue = productStockValue;
        this.productMovementHistory = productMovementHistory;
    }

    public ReportGenerator getGenerator(String type) {
        return switch (type.toUpperCase()) {
            case "INVENTORY_SUMMARY"        -> inventorySummary;
            case "SALES"                    -> salesReport;
            case "PURCHASES"               -> purchaseReport;
            case "STOCK_VALUATION"          -> stockValuation;
            case "EXPIRATION"               -> expirationReport;
            case "PRODUCT_STOCK_VALUE"      -> productStockValue;
            case "PRODUCT_MOVEMENT_HISTORY" -> productMovementHistory;
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + type);
        };
    }
}
