package com.LoQueHay.project.repository;

import com.LoQueHay.project.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
