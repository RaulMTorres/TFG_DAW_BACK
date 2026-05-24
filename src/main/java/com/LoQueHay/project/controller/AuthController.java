package com.LoQueHay.project.controller;

import com.LoQueHay.project.dto.auth_dtos.*;
import com.LoQueHay.project.service.AuthService;
import com.LoQueHay.project.service.PasswordResetService;
import com.LoQueHay.project.util.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthUtils authUtils;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, AuthUtils authUtils, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.authUtils = authUtils;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/register-owner")
    public ResponseEntity<?> registerOwner(@Valid @RequestBody RegisterRequest request) {
        authService.registerOwner(request);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }



    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestHeader("X-Owner-Id") Long ownerId, @Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request,ownerId);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        MyUserEntityDto myUserEntityDto = authService.getUserById(userId);
        return ResponseEntity.ok(myUserEntityDto);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
        MyUserEntityDto myUserEntityDto = authService.getUserById(authUtils.getCurrentUser().getId());
        return ResponseEntity.ok(myUserEntityDto);
    }


    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UpdateUserRequest request) {
        Long currentUserId = authUtils.getCurrentUser().getId();
        MyUserEntityDto updatedUser = authService.updateUserInfo(currentUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/user/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        Long currentUserId = authUtils.getCurrentUser().getId();
        authService.updatePassword(currentUserId, request);
        return ResponseEntity.ok("Password updated successfully");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        // Siempre devolver 200 para no revelar si el email existe
        return ResponseEntity.ok("Si el email está registrado, recibirás un enlace para restablecer tu contraseña.");
    }

    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        if (!valid) {
            return ResponseEntity.badRequest().body("Token inválido o expirado.");
        }
        return ResponseEntity.ok("Token válido.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Contraseña restablecida correctamente.");
    }

}
