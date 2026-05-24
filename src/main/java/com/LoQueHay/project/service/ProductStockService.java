package com.LoQueHay.project.service;

import com.LoQueHay.project.dto.dashboard_dtos.ProductsExpiringDTO;
import com.LoQueHay.project.dto.dashboard_dtos.StockByWarehouseDTO;
import com.LoQueHay.project.dto.product_stock_dtos.ProductStockRequestDTO;
import com.LoQueHay.project.exception.ResourceNotFoundException;
import com.LoQueHay.project.model.*;
import com.LoQueHay.project.repository.ProductStockRepository;
import com.LoQueHay.project.repository.WarehouseRepository;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductStockService {

    private final WarehouseService warehouseService;
    private final ProductStockRepository stockRepository;
    private final ProductService productService;
    private final AuthUtils authUtils;

    public ProductStockService(WarehouseService warehouseService, ProductStockRepository stockRepository, ProductService productService, AuthUtils authUtils) {
        this.warehouseService = warehouseService;
        this.stockRepository = stockRepository;
        this.productService = productService;
        this.authUtils = authUtils;
    }

    public ProductStock getById(Long id){
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));
    }




    public List<ProductStock> getAllByProduct(Long productId){
        productService.getById(productId); // valida existencia del producto
        return stockRepository.findByProductId(productId);
    }

    public List<ProductStock> getAllAvailableStocks() {
        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        return stockRepository.findByWarehouseOwnerIdAndQuantityGreaterThan(ownerId, 0);
    }


    public List<StockByWarehouseDTO>getTotalStockValueByWarehouse(){
        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        return stockRepository.getTotalStockValueByWarehouse(ownerId);
    }



    public boolean IsEnoughStock(Integer quantity, Product product, Warehouse warehouse){
        int stocks = stockRepository.findByProductAndWarehouse(product, warehouse).stream().mapToInt(ProductStock::getQuantity).sum();
        return stocks >= quantity;

    }

    public ProductStock save(ProductStock productStock) {
        return stockRepository.save(productStock);
    }

    @Transactional
    public ProductStock create(Long productId, ProductStockRequestDTO dto){
        Product product = productService.getById(productId);
        Warehouse warehouse = warehouseService.getById(dto.getWarehouseId());

        ProductStock stock = new ProductStock();
        stock.setProduct(product);
        stock.setWarehouse(warehouse);
        stock.setQuantity(dto.getQuantity());
        stock.setLotNumber((dto.getLotNumber() != null && !dto.getLotNumber().isEmpty())
                ? dto.getLotNumber()
                : "AUTO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        stock.setUnitCost(dto.getUnitCost());
        stock.setExpirationDate(dto.getExpirationDate());

        return stockRepository.save(stock);
    }


    @Transactional
    public ProductStock update(Long id, ProductStockRequestDTO dto){
        ProductStock existing = getById(id);
        existing.setQuantity(dto.getQuantity());
        existing.setLotNumber((dto.getLotNumber() != null && !dto.getLotNumber().isEmpty())
                ? dto.getLotNumber()
                : "AUTO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        existing.setExpirationDate(dto.getExpirationDate());
        return stockRepository.save(existing);
    }

    @Transactional
    public void delete(Long id){
        ProductStock existing = getById(id);
        stockRepository.delete(existing);
    }



    //Reports

    public Double calculateTotalCost(List<ProductStock> stocks) {
        return stocks.stream()
                .mapToDouble(ps->(double)ps.getQuantity()*ps.getUnitCost())
                .sum();
    }

    // Devuelve los lotes disponibles para un producto en un almacén, ordenados FIFO
    public List<ProductStock> getAvailableStocks(Product product, Warehouse warehouse) {
        return stockRepository.findByProductAndWarehouseAndQuantityGreaterThanOrderByCreatedAtAsc(product, warehouse, 0);
    }

    public Optional<ProductStock> findByProductWarehouseAndLot(Long productId, Long warehouseId, String lotNumber) {
        return stockRepository.findByProductIdAndWarehouseIdAndLotNumber(productId, warehouseId, lotNumber);
    }

    public long countDistinctProductsInStock() {
        return this.getAllAvailableStocks().stream()
                // Mapea cada ProductStock al objeto Product asociado
                .map(ProductStock::getProduct)
                // Asegura que solo se cuenten los productos únicos (distintos)
                .distinct()
                // Cuenta el número de productos únicos
                .count();
    }

    public List<ProductsExpiringDTO> getExpiringProductsByPeriod() {
        MyUserEntity currentUser = authUtils.getCurrentUser();
        Long ownerId = currentUser.getOwner() != null ? currentUser.getOwner().getId() : currentUser.getId();

        List<ProductStock> list = stockRepository.findAllWithExpirationDateByOwner(ownerId);

        List<ProductsExpiringDTO> dtos = new ArrayList<>();

        String periodo1 = "0-60";
        String periodo2 = "61-120";
        String periodo3 = "121-180";
        String periodo4 = "180+";

        long count1 = 0;
        long count2 = 0;
        long count3 = 0;
        long count4 = 0;

        LocalDate today = LocalDate.now();

        if (!list.isEmpty()) {
            for (ProductStock ps : list) {
                LocalDate expDate = ps.getExpirationDate();
                if (expDate != null) {
                    long daysUntilExpiration = java.time.temporal.ChronoUnit.DAYS.between(today, expDate);

                    if (daysUntilExpiration >= 0 && daysUntilExpiration <= 60) {
                        count1++;
                    } else if (daysUntilExpiration >= 61 && daysUntilExpiration <= 120) {
                        count2++;
                    } else if (daysUntilExpiration >= 121 && daysUntilExpiration <= 180) {
                        count3++;
                    } else if (daysUntilExpiration > 180) {
                        count4++;
                    }
                }
            }
        }

        dtos.add(new ProductsExpiringDTO(periodo1, count1));
        dtos.add(new ProductsExpiringDTO(periodo2, count2));
        dtos.add(new ProductsExpiringDTO(periodo3, count3));
        dtos.add(new ProductsExpiringDTO(periodo4, count4));

        return dtos;
    }


}
