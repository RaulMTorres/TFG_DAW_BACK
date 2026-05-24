package com.LoQueHay.project.exception;

import com.LoQueHay.project.dto.exception_dtos.ExceptionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleResourceNotFound(ResourceNotFoundException ex) {

        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                "RESOURCE_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);


    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDTO> handleBadRequest(BadRequestException ex) {

        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Solicitud incorrecta",
                "BAD_REQUEST",
                ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ExceptionDTO> handleDuplicateResource(DuplicateResourceException ex) {
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.CONFLICT,
                "Duplicate resource",
                "VALIDATION_ERROR",
                ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException ex) {


        String validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                "VALIDATION_ERROR",
                validationErrors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "User not found");
        body.put("message", ex.getMessage());
        body.put("path", "/products");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePermissionNotFound(PermissionNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "Permission not found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRoleNotFound(RoleNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 404);
        body.put("error", "Role not found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PriceConflictException.class)
    public ResponseEntity<ExceptionDTO> handlePriceConflict(PriceConflictException ex) {
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.CONFLICT,
                "Conflicto de precios",
                "PRICE_CONFLICT",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ExceptionDTO> handleInsufficientStock(InsufficientStockException ex) {

        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Stock insuficiente",
                "INSUFFICIENT_STOCK",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(InvalidMovementTypeException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidMovementTypeException(InvalidMovementTypeException ex) {

        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Error en los datos enviados",
                "INVALID_MOVEMENT_TYPE",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(InvalidMovementDetailsException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidMovementDetails(InvalidMovementDetailsException ex) {
        ExceptionDTO error = new ExceptionDTO(
                HttpStatus.BAD_REQUEST,
                "Error en los detalles del movimiento",
                "INVALID_MOVEMENT_DETAILS",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
