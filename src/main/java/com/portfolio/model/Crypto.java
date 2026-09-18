package com.portfolio.model;

import java.time.LocalDate;

/**
 * Represents a Cryptocurrency asset.
 * Demonstrates: Inheritance, Method Overriding (Unit 2)
 */
public class Crypto extends Asset {
    private String blockchain;
    private String walletAddress;

    public Crypto(int id, String symbol, String name, double quantity, double buyPrice,
                  LocalDate buyDate, String currency, String blockchain, String walletAddress) {
        super(id, symbol, name, quantity, buyPrice, buyDate, currency);
        this.blockchain = blockchain;
        this.walletAddress = walletAddress;
    }

    @Override
    public String getAssetType() {
        return "CRYPTO";
    }

    @Override
    public double calculateCurrentValue(double currentPrice) {
        // Crypto supports fractional holdings
        return quantity * currentPrice;
    }

    @Override
    public String getSummary() {
        return String.format("Crypto: %s | Blockchain: %s | Holdings: %.8f coins",
                name, blockchain, quantity);
    }

    public String getBlockchain() { return blockchain; }
    public void setBlockchain(String blockchain) { this.blockchain = blockchain; }
    public String getWalletAddress() { return walletAddress; }
    public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }
}
