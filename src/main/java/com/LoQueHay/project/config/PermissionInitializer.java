package com.LoQueHay.project.config;

import com.LoQueHay.project.model.Permission;
import com.LoQueHay.project.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermissionInitializer {

    private final PermissionRepository permissionRepository;

    public PermissionInitializer(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @PostConstruct
    public void initPermissions() {
        Set<String> allPermissions = Set.of(
                "category.create", "category.read", "category.update", "category.delete",
                "product.create", "product.read", "product.update", "product.delete",
                "inventory.inbound.create", "inventory.inbound.read", "inventory.inbound.update", "inventory.inbound.delete",
                "inventory.outbound.create", "inventory.outbound.read", "inventory.outbound.update", "inventory.outbound.delete",
                "order.create", "order.read", "order.update", "order.delete",
                "provider.create", "provider.read", "provider.update", "provider.delete",
                "report.create", "report.read", "report.update", "report.delete",
                "report.export"
        );

        allPermissions.forEach(code -> {
            if (!permissionRepository.existsById(code)) {
                Permission permission = new Permission();
                permission.setCode(code);
                permissionRepository.save(permission);
            }
        });
    }
}
