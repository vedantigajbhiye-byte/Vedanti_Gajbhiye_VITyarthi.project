package com.portfolio.model;

import java.time.LocalDate;

/**
 * Represents a Stock asset.
 * Demonstrates: Inheritance from Asset, Method Overriding (Unit 2)
 */
public class Stock extends Asset {
    private String exchange;   // e.g., NSE, BSE, NYSE
    private String sector;

    public Stock(int id, String symbol, String name, double quantity, double buyPrice,
                 LocalDate buyDate, String currency, String exchange, String sector) {
        super(id, symbol, name, quantity, buyPrice, buyDate, currency);
        this.exchange = exchange;
        this.sector = sector;
    }

    @Override
    public String getAssetType() {
        return "STOCK";
    }

    @Override
    public double calculateCurrentValue(double currentPrice) {
        return quantity * currentPrice;
    }

    @Override
    public String getSummary() {
        return String.format("Stock: %s | Exchange: %s | Sector: %s | Shares: %.2f",
                name, exchange, sector, quantity);
    }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
}
