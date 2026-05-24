package com.LoQueHay.project.service;

import com.LoQueHay.project.exception.BadRequestException;
import com.LoQueHay.project.exception.ResourceNotFoundException;
import com.LoQueHay.project.model.MyUserEntity;
import com.LoQueHay.project.model.PasswordResetToken;
import com.LoQueHay.project.repository.MyUserEntityRepository;
import com.LoQueHay.project.repository.PasswordResetTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_EXPIRY_MINUTES = 30;

    private final MyUserEntityRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@app.com}")
    private String fromEmail;

    public PasswordResetService(
            MyUserEntityRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    /**
     * Genera un token de reset y envía el email.
     * Si el email no existe, no se lanza error (seguridad: no revelar usuarios).
     */
    @Transactional
    public void requestPasswordReset(String email) {
        MyUserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Por seguridad no revelamos si el email existe o no
            log.info("Reset solicitado para email no registrado: {}", email);
            return;
        }

        // Eliminar tokens anteriores del usuario
        tokenRepository.deleteByUserId(user.getId());

        // Crear nuevo token
        String rawToken = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(rawToken);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES));
        tokenRepository.save(resetToken);

        // Enviar email
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;
        sendResetEmail(user.getEmail(), user.getFirstName(), resetLink);

        log.info("Token de reset generado para usuario: {} | Token: {}", user.getEmail(), rawToken);
    }

    private void sendResetEmail(String to, String firstName, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Restablecer contraseña");
            message.setText(
                "Hola " + (firstName != null ? firstName : "") + ",\n\n" +
                "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                "Haz clic en el siguiente enlace (válido por " + TOKEN_EXPIRY_MINUTES + " minutos):\n\n" +
                resetLink + "\n\n" +
                "Si no solicitaste este cambio, puedes ignorar este mensaje.\n\n" +
                "Saludos."
            );
            mailSender.send(message);
        } catch (Exception e) {
            // Loguear pero no fallar — el token ya está guardado
            log.error("Error al enviar email de reset a {}: {}", to, e.getMessage());
        }
    }

    /**
     * Valida el token y cambia la contraseña.
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        if (resetToken.isExpired()) {
            throw new BadRequestException("El enlace de recuperación ha expirado. Solicita uno nuevo.");
        }

        if (resetToken.isUsed()) {
            throw new BadRequestException("Este enlace ya fue utilizado.");
        }

        MyUserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Contraseña restablecida para usuario: {}", user.getEmail());
    }

    /**
     * Valida si un token es válido (para el frontend, antes de mostrar el formulario).
     */
    public boolean validateToken(String rawToken) {
        return tokenRepository.findByToken(rawToken)
                .map(t -> !t.isExpired() && !t.isUsed())
                .orElse(false);
    }
}
