package com.portfolio.model;

import java.time.LocalDate;

/**
 * Abstract base class for all asset types.
 * Demonstrates: Abstract class, Encapsulation, OOP principles (Unit 2)
 */
public abstract class Asset {
    protected int id;
    protected String symbol;
    protected String name;
    protected double quantity;
    protected double buyPrice;
    protected LocalDate buyDate;
    protected String currency;

    // Constructor
    public Asset(int id, String symbol, String name, double quantity, double buyPrice, LocalDate buyDate, String currency) {
        this.id = id;
        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
        this.currency = currency;
    }

    // Abstract methods - must be implemented by subclasses (Polymorphism)
    public abstract String getAssetType();
    public abstract double calculateCurrentValue(double currentPrice);
    public abstract String getSummary();

    // Common methods
    public double getTotalInvestment() {
        return quantity * buyPrice;
    }

    public double getProfitLoss(double currentPrice) {
        return calculateCurrentValue(currentPrice) - getTotalInvestment();
    }

    public double getProfitLossPercent(double currentPrice) {
        if (getTotalInvestment() == 0) return 0;
        return (getProfitLoss(currentPrice) / getTotalInvestment()) * 100;
    }

    // Getters and Setters (Encapsulation)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    public LocalDate getBuyDate() { return buyDate; }
    public void setBuyDate(LocalDate buyDate) { this.buyDate = buyDate; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Qty: %.4f | Buy Price: %.2f | Date: %s",
                getAssetType(), name, symbol, quantity, buyPrice, buyDate);
    }
}
