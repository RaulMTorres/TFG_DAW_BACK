package com.LoQueHay.project.service;


import com.LoQueHay.project.dto.auth_dtos.*;
import com.LoQueHay.project.exception.*;
import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.model.Role;
import com.LoQueHay.project.repository.MyUserEntityRepository;
import com.LoQueHay.project.repository.RoleRepository;
import com.LoQueHay.project.security.JwtService;
import com.LoQueHay.project.util.AuthUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.lang.Nullable;
import java.util.Set;


@Service
public class AuthService {

    private final RoleRepository roleRepository;
    private final MyUserEntityRepository myUserEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PermissionService permissionService;
    private final AuthUtils authUtils;

    public AuthService(RoleRepository roleRepository, MyUserEntityRepository myUserEntityRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, PermissionService permissionService, AuthUtils authUtils) {
        this.roleRepository = roleRepository;
        this.myUserEntityRepository = myUserEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.permissionService = permissionService;
        this.authUtils = authUtils;
    }

    public AuthResponse login(AuthRequest authRequest) {
        try {
            // 1. Autenticar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // 2. Buscar el usuario
        MyUserEntity user = myUserEntityRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Generar token
        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public MyUserEntity registerOwner(RegisterRequest req) {
        return register(req, "OWNER", null);
    }

    public MyUserEntity registerUser(RegisterRequest req, Long ownerId) {
        return register(req, "USER", ownerId);
    }

    public MyUserEntityDto getUserById(@PathVariable Long id) {
        MyUserEntity user = myUserEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Set<String> permissions = permissionService.getEffectivePermissions(user);

        MyUserEntityDto dto = new MyUserEntityDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setLocked(user.isLocked());
        dto.setCreated_by(user.getCreated_by());
        dto.setUpdated_by(user.getUpdated_by());
        dto.setEffectivePermissions(permissions);

        return dto;
    }



    public MyUserEntity register(RegisterRequest req,
                                 String roleName,
                                 @Nullable Long ownerId) {

        // 1- Validar duplicados
        assertUniqueEmailAndUsername(req.getEmail(), req.getUsername());

        // 2- Construir usuario base
        MyUserEntity user = buildBaseUser(req);

        // 3- Rol
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role " + roleName + " not found"));

        // 4- Owner
        if (ownerId == null) {                           // registro de Owner
            user = myUserEntityRepository.save(user);    // guardamos primero
            user.setOwner(user);                         // se auto-posee
        } else {                                         // registro de User normal
            MyUserEntity owner = myUserEntityRepository.findById(ownerId)
                    .orElseThrow(() -> new UserNotFoundException("Owner not found"));
            user.setOwner(owner);
            user.setCreated_by(owner.getId());
            user.setUpdated_by(owner.getId());
        }

        // 5- Persistir (segunda vez si era owner)
        return myUserEntityRepository.save(user);
    }


    private void assertUniqueEmailAndUsername(String email, String username) {
        if (myUserEntityRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if (myUserEntityRepository.findByUsername(username).isPresent()) {
            throw new DuplicateResourceException("Username is already in use");
        }
    }


    private MyUserEntity buildBaseUser(RegisterRequest req) {
        MyUserEntity user = new MyUserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEnabled(true);
        user.setLocked(false);
        return user;
    }



    public MyUserEntityDto updateUserInfo(Long userId, UpdateUserRequest req) {
        MyUserEntity user = myUserEntityRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validar duplicados (exceptuando el usuario actual)
        myUserEntityRepository.findByEmail(req.getEmail())
                .filter(u -> !Long.valueOf(u.getId()).equals(userId))
                .ifPresent(u -> { throw new DuplicateResourceException("Email is already in use"); });

        myUserEntityRepository.findByUsername(req.getUsername())
                .filter(u -> !Long.valueOf(u.getId()).equals(userId))
                .ifPresent(u -> { throw new DuplicateResourceException("Username is already in use"); });

        // Actualizar campos
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setUpdated_by(authUtils.getCurrentUser().getId()); // registrar quién actualizó

        MyUserEntity updatedUser = myUserEntityRepository.save(user);

        // Mapear a DTO
        MyUserEntityDto dto = new MyUserEntityDto();
        dto.setId(updatedUser.getId());
        dto.setFirstName(updatedUser.getFirstName());
        dto.setLastName(updatedUser.getLastName());
        dto.setUsername(updatedUser.getUsername());
        dto.setEmail(updatedUser.getEmail());
        dto.setEnabled(updatedUser.isEnabled());
        dto.setLocked(updatedUser.isLocked());
        dto.setCreated_by(updatedUser.getCreated_by());
        dto.setUpdated_by(updatedUser.getUpdated_by());

        return dto;
    }

    public void updatePassword(Long userId, UpdatePasswordRequest req) {
        MyUserEntity user = myUserEntityRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Actualizar password
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setUpdated_by(authUtils.getCurrentUser().getId());

        myUserEntityRepository.save(user);
    }


}
