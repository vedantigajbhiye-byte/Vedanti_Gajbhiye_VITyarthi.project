package com.portfolio.model;

import java.time.LocalDate;

/**
 * Represents a Mutual Fund asset.
 * Demonstrates: Inheritance, Method Overriding (Unit 2)
 */
public class MutualFund extends Asset {
    private String fundHouse;
    private String category;  // e.g., Large Cap, Mid Cap, Debt
    private double expenseRatio;

    public MutualFund(int id, String symbol, String name, double quantity, double buyPrice,
                      LocalDate buyDate, String currency, String fundHouse,
                      String category, double expenseRatio) {
        super(id, symbol, name, quantity, buyPrice, buyDate, currency);
        this.fundHouse = fundHouse;
        this.category = category;
        this.expenseRatio = expenseRatio;
    }

    @Override
    public String getAssetType() {
        return "MUTUAL_FUND";
    }

    @Override
    public double calculateCurrentValue(double currentNAV) {
        // Units * Current NAV
        return quantity * currentNAV;
    }

    @Override
    public String getSummary() {
        return String.format("Mutual Fund: %s | Fund House: %s | Category: %s | Units: %.4f | Expense Ratio: %.2f%%",
                name, fundHouse, category, quantity, expenseRatio);
    }

    public String getFundHouse() { return fundHouse; }
    public void setFundHouse(String fundHouse) { this.fundHouse = fundHouse; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getExpenseRatio() { return expenseRatio; }
    public void setExpenseRatio(double expenseRatio) { this.expenseRatio = expenseRatio; }
}
