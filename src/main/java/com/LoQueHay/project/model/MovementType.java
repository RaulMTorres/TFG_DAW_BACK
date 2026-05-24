package com.LoQueHay.project.model;

import com.LoQueHay.project.exception.InvalidMovementTypeException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum MovementType {
    IN, OUT, TRANSFER;

    @JsonCreator
    public static MovementType fromString(String movementType) {
        try {
            return MovementType.valueOf(movementType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMovementTypeException("Tipo de movimiento inválido. Solo se permiten IN,OUT,TRANSFER");
        }
    }
}
