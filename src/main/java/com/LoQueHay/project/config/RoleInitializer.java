package com.LoQueHay.project.config;

import com.LoQueHay.project.model.Role;
import com.LoQueHay.project.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Set;
@Component
public class RoleInitializer {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        createRoleIfNotExists("PLATFORM_ADMIN");
        createRoleIfNotExists("OWNER");
        createRoleIfNotExists("USER");
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsById(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            role.setPermissions(Set.of()); // o podrías agregar permisos base aquí
            roleRepository.save(role);
        }
    }
}
