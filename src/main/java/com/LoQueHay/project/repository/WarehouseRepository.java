package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.Category;
import com.LoQueHay.project.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Warehouse> findByOwnerIdAndNameContainingIgnoreCase(Long ownerId, String name, Pageable pageable);

    List<Warehouse> findByOwnerId(Long ownerId);

}
