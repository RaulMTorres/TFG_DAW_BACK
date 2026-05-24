package com.LoQueHay.project.service;

import com.LoQueHay.project.exception.PermissionNotFoundException;
import com.LoQueHay.project.exception.RoleNotFoundException;
import com.LoQueHay.project.exception.UserNotFoundException;
import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.model.Permission;
import com.LoQueHay.project.model.Role;
import com.LoQueHay.project.repository.MyUserEntityRepository;
import com.LoQueHay.project.repository.PermissionRepository;
import com.LoQueHay.project.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MyUserEntityRepository userRepository;

    public PermissionService(RoleRepository roleRepository,
                             PermissionRepository permissionRepository,
                             MyUserEntityRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    /**
     * ======================
     * PERMISOS EN ROLES
     * ======================
     */
    @Transactional
    public void addPermissionsToRole(String roleName, Set<String> permissionCodes) {
        Role role = roleRepository.findById(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found"));

        for (String code : permissionCodes) {
            Permission permission = permissionRepository.findById(code).orElseThrow(() -> new PermissionNotFoundException("Permission not found: " + code));
            role.getPermissions().add(permission);
        }

        roleRepository.save(role);
    }

    @Transactional
    public void removePermissionsFromRole(String roleName, Set<String> permissionCodes) {
        Role role = roleRepository.findById(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found"));

        role.getPermissions().removeIf(permission -> permissionCodes.contains(permission.getCode()));

        roleRepository.save(role);
    }

    public Set<Permission> listPermissionsOfRole(String roleName) {
        Optional<Role> roleOptional = roleRepository.findById(roleName);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            return role.getPermissions();
        } else {
            throw new RoleNotFoundException("Role not found");
        }
    }

    /**
     * ==========================
     * PERMISOS ADICIONALES EN USUARIOS
     * ==========================
     */
    @Transactional
    public void addPermissionsToUser(Long userId, Set<String> permissionCodes) {
        MyUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        for (String code : permissionCodes) {
            Permission permission = permissionRepository.findById(code).orElseThrow(() -> new PermissionNotFoundException("Permission not found: " + code));
            user.getAdditionalPermissions().add(permission);
        }

        userRepository.save(user);
    }

    @Transactional
    public void removePermissionsFromUser(Long userId, Set<String> permissionCodes) {
        MyUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.getAdditionalPermissions().removeIf(permission -> permissionCodes.contains(permission.getCode()));

        userRepository.save(user);
    }

    public Set<Permission> listUserDirectPermissions(Long userId) {
        Optional<MyUserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getAdditionalPermissions();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    /**
     * ==========================
     * PERMISOS REVOCADOS EN USUARIOS
     * ==========================
     */
    @Transactional
    public void revokePermissionsFromUser(Long userId, Set<String> permissionCodes) {
        MyUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        for (String code : permissionCodes) {
            Permission permission = permissionRepository.findById(code).orElseThrow(() -> new PermissionNotFoundException("Permission not found: " + code));
            user.getRevokedPermissions().add(permission);
        }

        userRepository.save(user);
    }

    @Transactional
    public void restoreRevokedPermissions(Long userId, Set<String> permissionCodes) {
        MyUserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        user.getRevokedPermissions().removeIf(permission -> permissionCodes.contains(permission.getCode()));

        userRepository.save(user);
    }

    public Set<Permission> listUserRevokedPermissions(Long userId) {
        Optional<MyUserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getRevokedPermissions();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    /**
     * ==========================
     * PERMISOS EFECTIVOS DEL USUARIO
     * ==========================
     */
    public Set<String> getEffectivePermissions(MyUserEntity user) {
        Set<String> effectivePermissions = new HashSet<>();

        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                effectivePermissions.add(permission.getCode());
            }
        }

        for (Permission permission : user.getAdditionalPermissions()) {
            effectivePermissions.add(permission.getCode());
        }

        for (Permission permission : user.getRevokedPermissions()) {
            effectivePermissions.remove(permission.getCode());
        }

        return effectivePermissions;
    }
}
