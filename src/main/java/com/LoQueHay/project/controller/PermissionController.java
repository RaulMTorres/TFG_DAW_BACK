package com.LoQueHay.project.controller;

import com.LoQueHay.project.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    // 🔹 Agregar permisos a un usuario
    @PostMapping("/user/{userId}/add")
    public ResponseEntity<?> addPermissionsToUser(
            @PathVariable Long userId,
            @RequestBody Set<String> permissionCodes) {
        permissionService.addPermissionsToUser(userId, permissionCodes);
        return ResponseEntity.ok("Permisos agregados al usuario.");
    }

    // 🔹 Revocar permisos de un usuario
    @PostMapping("/user/{userId}/revoke")
    public ResponseEntity<?> revokePermissionsFromUser(
            @PathVariable Long userId,
            @RequestBody Set<String> permissionCodes) {
        permissionService.removePermissionsFromUser(userId, permissionCodes);
        return ResponseEntity.ok("Permisos revocados del usuario.");
    }

    // 🔹 Eliminar permisos adicionales de un usuario (los que se agregaron manualmente)
    @PostMapping("/user/{userId}/remove")
    public ResponseEntity<?> removePermissionsFromUser(
            @PathVariable Long userId,
            @RequestBody Set<String> permissionCodes) {
        permissionService.removePermissionsFromUser(userId, permissionCodes);
        return ResponseEntity.ok("Permisos eliminados del usuario.");
    }

    // 🔹 Agregar permisos a un rol
    @PostMapping("/role/{roleName}/add")
    public ResponseEntity<?> addPermissionsToRole(
            @PathVariable String roleName,
            @RequestBody Set<String> permissionCodes) {
        permissionService.addPermissionsToRole(roleName, permissionCodes);
        return ResponseEntity.ok("Permisos agregados al rol.");
    }

    // 🔹 Eliminar permisos de un rol
    @PostMapping("/role/{roleName}/remove")
    public ResponseEntity<?> removePermissionsFromRole(
            @PathVariable String roleName,
            @RequestBody Set<String> permissionCodes) {
        permissionService.removePermissionsFromRole(roleName, permissionCodes);
        return ResponseEntity.ok("Permisos eliminados del rol.");
    }
}

