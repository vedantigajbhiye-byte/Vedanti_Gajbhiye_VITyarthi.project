package com.portfolio.model;

/**
 * Holds aggregated portfolio statistics.
 * Demonstrates: Java Classes, Encapsulation (Unit 2)
 */
public class PortfolioSummary {
    private double totalInvested;
    private double totalCurrentValue;
    private double totalProfitLoss;
    private double overallReturnPercent;
    private int totalAssets;
    private int stockCount;
    private int cryptoCount;
    private int mutualFundCount;

    public PortfolioSummary() {
        this.totalInvested = 0;
        this.totalCurrentValue = 0;
        this.totalProfitLoss = 0;
        this.overallReturnPercent = 0;
        this.totalAssets = 0;
        this.stockCount = 0;
        this.cryptoCount = 0;
        this.mutualFundCount = 0;
    }

    public void addAsset(Asset asset, double currentPrice) {
        totalInvested += asset.getTotalInvestment();
        totalCurrentValue += asset.calculateCurrentValue(currentPrice);
        totalProfitLoss = totalCurrentValue - totalInvested;
        if (totalInvested > 0)
            overallReturnPercent = (totalProfitLoss / totalInvested) * 100;
        totalAssets++;

        if (asset instanceof Stock) stockCount++;
        else if (asset instanceof Crypto) cryptoCount++;
        else if (asset instanceof MutualFund) mutualFundCount++;
    }

    // Getters
    public double getTotalInvested() { return totalInvested; }
    public double getTotalCurrentValue() { return totalCurrentValue; }
    public double getTotalProfitLoss() { return totalProfitLoss; }
    public double getOverallReturnPercent() { return overallReturnPercent; }
    public int getTotalAssets() { return totalAssets; }
    public int getStockCount() { return stockCount; }
    public int getCryptoCount() { return cryptoCount; }
    public int getMutualFundCount() { return mutualFundCount; }

    @Override
    public String toString() {
        return String.format(
            "Total Invested: %.2f | Current Value: %.2f | P&L: %.2f (%.2f%%)",
            totalInvested, totalCurrentValue, totalProfitLoss, overallReturnPercent
        );
    }
}
