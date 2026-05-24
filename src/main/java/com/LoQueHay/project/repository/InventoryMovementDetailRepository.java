package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.InventoryMovementDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementDetailRepository extends JpaRepository<InventoryMovementDetail, Long> {

    boolean existsByProductIdAndMovementOwnerId(Long productId, Long ownerId);

}
