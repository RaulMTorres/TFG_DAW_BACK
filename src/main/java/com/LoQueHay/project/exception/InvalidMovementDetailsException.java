package com.LoQueHay.project.exception;

public class InvalidMovementDetailsException extends RuntimeException {
    public InvalidMovementDetailsException(String message) {
        super(message);
    }
}