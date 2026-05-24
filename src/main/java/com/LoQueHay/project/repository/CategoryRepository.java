package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Category> findByOwnerIdAndNameContainingIgnoreCase(Long ownerId, String name, Pageable pageable);

    List<Category> findByOwnerId(Long ownerId);
}

