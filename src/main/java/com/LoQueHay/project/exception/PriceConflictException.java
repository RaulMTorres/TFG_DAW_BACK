package com.LoQueHay.project.exception;

public class PriceConflictException extends RuntimeException {
    private final Double conflictingPrice;
    private final String currency;
    private final String validFrom;
    private final String validTo;

    public PriceConflictException(Double price, String currency, String validFrom, String validTo) {
        super("Ya existe un precio activo: " + price + " " + currency +
                " desde " + validFrom + " hasta " + validTo);
        this.conflictingPrice = price;
        this.currency = currency;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Double getConflictingPrice() { return conflictingPrice; }
    public String getCurrency() { return currency; }
    public String getValidFrom() { return validFrom; }
    public String getValidTo() { return validTo; }
}
