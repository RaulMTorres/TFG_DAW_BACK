package com.LoQueHay.project.repository;

import com.LoQueHay.project.dto.dashboard_dtos.MonthlyMovementSummaryDTO;
import com.LoQueHay.project.dto.dashboard_dtos.MonthlySalesPurchasesDTO;
import com.LoQueHay.project.model.InventoryMovement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryMovementRepository
        extends JpaRepository<InventoryMovement, Long>, JpaSpecificationExecutor<InventoryMovement> {


    Optional<InventoryMovement> findByIdAndOwnerId(Long id, Long ownerId);

    // Contar ventas del último mes
    @Query("SELECT COUNT(im) FROM InventoryMovement im " +
            "WHERE im.owner.id = :ownerId " +
            "AND im.movementType = 'OUT' " +
            "AND im.createdAt >= :startDate")
    Long countSalesLastMonth(@Param("ownerId") Long ownerId,
                             @Param("startDate") LocalDateTime startDate);

    // Contar entradas del último mes
    @Query("SELECT COUNT(im) FROM InventoryMovement im " +
            "WHERE im.owner.id = :ownerId " +
            "AND im.movementType = 'IN' " +
            "AND im.createdAt >= :startDate")
    Long countPurchasesLastMonth(@Param("ownerId") Long ownerId,
                                 @Param("startDate") LocalDateTime startDate);


    @Query("""
    SELECT im 
    FROM InventoryMovement im
    JOIN FETCH im.details d
    WHERE im.owner.id = :ownerId
    AND im.createdAt >= :startDate
""")
    List<InventoryMovement> findAllMovementsWithDetails(
            @Param("ownerId") Long ownerId,
            @Param("startDate") LocalDateTime startDate
    );
}