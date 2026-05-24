package com.LoQueHay.project.service.reports;

import com.LoQueHay.project.dto.dashboard_dtos.CategoryStockDTO;
import com.LoQueHay.project.dto.dashboard_dtos.DashboardSummaryDTO;
import com.LoQueHay.project.dto.dashboard_dtos.ProductsExpiringDTO;
import com.LoQueHay.project.dto.dashboard_dtos.StockByWarehouseDTO;
import com.LoQueHay.project.dto.report_dtos.ReportRequestDTO;
import com.LoQueHay.project.model.MovementType;
import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.service.CategoryService;
import com.LoQueHay.project.service.InventoryMovementService;
import com.LoQueHay.project.service.ProductService;
import com.LoQueHay.project.service.ProductStockService;
import com.LoQueHay.project.service.reports.generators.ReportGenerator;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final ProductStockService productStockService;
    private final InventoryMovementService inventoryMovementService;
    private final ReportFactory reportFactory;
    private final AuthUtils authUtils;
    private final CategoryService categoryService;
    private final ProductService productService;

    public ReportService(ProductStockService productStockService, InventoryMovementService inventoryMovementService, ReportFactory reportFactory, AuthUtils authUtils, CategoryService categoryService, ProductService productService) {
        this.productStockService = productStockService;
        this.inventoryMovementService = inventoryMovementService;
        this.reportFactory = reportFactory;
        this.authUtils = authUtils;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    public byte[] generateReport(ReportRequestDTO request) {
        request.setOwnerId(authUtils.getCurrentUser().getOwner().getId());
        ReportGenerator generator = reportFactory.getGenerator(request.getReportType());
        return generator.generate(request);
    }


    // Costo total del alamacen
    public Double getTotalCost(){
        return productStockService.calculateTotalCost(productStockService.getAllAvailableStocks());

    }

    //Cantidad de productos en stock(la variedad)
    public long getCountDistinctProductsInStock() {
        return productStockService.countDistinctProductsInStock();
    }



    public DashboardSummaryDTO getSummary(){
        DashboardSummaryDTO summaryDTO = new DashboardSummaryDTO();
        summaryDTO.setTotalInventoryValue(this.getTotalCost());
        summaryDTO.setTotalProductsInStock(this.getCountDistinctProductsInStock());
        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();
        summaryDTO.setPurchasesThisMonth(inventoryMovementService.getPurchasesThisMonth(ownerId));
        summaryDTO.setSalesThisMonth(inventoryMovementService.getSalesThisMonth(ownerId));
        return summaryDTO;
    }

    public List<CategoryStockDTO> getCategoryStock(){
        return categoryService.getAll().stream().map(c->new CategoryStockDTO(c.getName(),productService.countByCategory(c))).toList();
    }

    public List<StockByWarehouseDTO> getStockByWarehouse(){
        return productStockService.getTotalStockValueByWarehouse();
    }

    public List<ProductsExpiringDTO> getExpiringProductsByPeriod(){
        return productStockService.getExpiringProductsByPeriod();
    }





}
