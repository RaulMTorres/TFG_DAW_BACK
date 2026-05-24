package com.LoQueHay.project.exception;

public class InsufficientStockException extends RuntimeException {

    private final Long productId;

    public InsufficientStockException(Long productId, String productName) {
        super("No hay suficiente stock para el producto: " + productName.replace("\"", "").trim());
        this.productId = productId;
    }

    public Long getProductId() { return productId; }
}