package com.LoQueHay.project.repository;

import com.LoQueHay.project.dto.dashboard_dtos.ProductsExpiringDTO;
import com.LoQueHay.project.dto.dashboard_dtos.StockByWarehouseDTO;
import com.LoQueHay.project.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long>, JpaSpecificationExecutor<ProductStock> {

    List<ProductStock> findByProductAndWarehouse(Product product, Warehouse warehouse);
    Optional<ProductStock> findByProductIdAndWarehouseIdAndLotNumber(Long productId, Long warehouseId, String lotNumber);
    List<ProductStock> findByProductId(Long id);

    // Obtiene todos los stocks disponibles de un producto en un almacén con cantidad > 0
    // Ordenados por createdAt ascendente (FIFO)
    List<ProductStock> findByProductAndWarehouseAndQuantityGreaterThanOrderByCreatedAtAsc(
            Product product, Warehouse warehouse, Integer quantityThreshold
    );

    //Reports
    List<ProductStock> findByWarehouseOwnerIdAndQuantityGreaterThan(Long ownerId, Integer quantity);

    boolean existsByProductIdAndWarehouseOwnerIdAndQuantityGreaterThan(Long productId, Long ownerId, Integer qty);


    @Query("""
      SELECT new com.LoQueHay.project.dto.dashboard_dtos.StockByWarehouseDTO(
        ps.warehouse.name,
        SUM(ps.quantity * ps.unitCost)
      )
      FROM ProductStock ps
      WHERE ps.warehouse.owner.id = :ownerId
        AND ps.quantity > 0
      GROUP BY ps.warehouse.name
    """)
    List<StockByWarehouseDTO> getTotalStockValueByWarehouse(@Param("ownerId") Long ownerId);



    @Query("""
      SELECT ps
      FROM ProductStock ps
      WHERE ps.expirationDate IS NOT NULL
        AND ps.warehouse.owner.id = :ownerId
        AND ps.quantity > 0
    """)
    List<ProductStock> findAllWithExpirationDateByOwner(@Param("ownerId") Long ownerId);

    boolean existsByWarehouseId(Long warehouseId);

}
