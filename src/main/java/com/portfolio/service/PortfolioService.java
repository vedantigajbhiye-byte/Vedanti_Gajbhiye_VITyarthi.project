package com.portfolio.service;

import com.portfolio.dao.AssetDAO;
import com.portfolio.exception.AssetNotFoundException;
import com.portfolio.exception.InvalidInputException;
import com.portfolio.model.*;
import com.portfolio.util.InputValidator;
import com.portfolio.util.ReportWriter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer - business logic for portfolio operations.
 * Demonstrates: Java Collections (ArrayList, HashMap, TreeMap),
 *               Java List operations, Sorting (Unit 4)
 */
public class PortfolioService {

    private final AssetDAO assetDAO;

    // Collections Framework usage: ArrayList to hold assets in memory
    private List<Asset> cachedAssets = new ArrayList<>();

    // HashMap for current prices: assetId -> currentPrice
    private Map<Integer, Double> currentPrices = new HashMap<>();

    public PortfolioService() {
        this.assetDAO = new AssetDAO();
        refreshCache();
    }

    // Load all assets from DB into memory
    private void refreshCache() {
        try {
            cachedAssets = assetDAO.getAllAssets();
            currentPrices = assetDAO.getLatestPrices();
        } catch (SQLException e) {
            System.err.println("[SERVICE ERROR] Could not load assets: " + e.getMessage());
            cachedAssets = new ArrayList<>();
            currentPrices = new HashMap<>();
        }
    }

    // ─── ADD ASSET ─────────────────────────────────────────────────────────────

    public Asset addStock(String symbol, String name, String quantityStr, String priceStr,
                          String dateStr, String currency, String exchange, String sector)
            throws InvalidInputException, SQLException {
        double quantity = InputValidator.validatePositiveDouble(quantityStr, "quantity");
        double price = InputValidator.validatePositiveDouble(priceStr, "buy price");
        LocalDate date = InputValidator.validateDate(dateStr);
        InputValidator.validateNonEmpty(symbol, "symbol");
        InputValidator.validateNonEmpty(name, "name");

        Stock stock = new Stock(0, symbol, name, quantity, price, date,
                currency.toUpperCase(), exchange, sector);
        assetDAO.addAsset(stock);
        refreshCache();
        return stock;
    }

    public Asset addCrypto(String symbol, String name, String quantityStr, String priceStr,
                           String dateStr, String currency, String blockchain, String wallet)
            throws InvalidInputException, SQLException {
        double quantity = InputValidator.validatePositiveDouble(quantityStr, "quantity");
        double price = InputValidator.validatePositiveDouble(priceStr, "buy price");
        LocalDate date = InputValidator.validateDate(dateStr);
        InputValidator.validateNonEmpty(symbol, "symbol");
        InputValidator.validateNonEmpty(name, "name");

        Crypto crypto = new Crypto(0, symbol, name, quantity, price, date,
                currency.toUpperCase(), blockchain, wallet);
        assetDAO.addAsset(crypto);
        refreshCache();
        return crypto;
    }

    public Asset addMutualFund(String symbol, String name, String quantityStr, String priceStr,
                               String dateStr, String currency, String fundHouse,
                               String category, String expenseStr)
            throws InvalidInputException, SQLException {
        double quantity = InputValidator.validatePositiveDouble(quantityStr, "units");
        double price = InputValidator.validatePositiveDouble(priceStr, "NAV");
        LocalDate date = InputValidator.validateDate(dateStr);
        double expense = InputValidator.validatePositiveDouble(expenseStr, "expense ratio");
        InputValidator.validateNonEmpty(symbol, "symbol");
        InputValidator.validateNonEmpty(name, "name");

        MutualFund mf = new MutualFund(0, symbol, name, quantity, price, date,
                currency.toUpperCase(), fundHouse, category, expense);
        assetDAO.addAsset(mf);
        refreshCache();
        return mf;
    }

    // ─── DELETE ASSET ──────────────────────────────────────────────────────────

    public void deleteAsset(int id) throws SQLException, AssetNotFoundException {
        assetDAO.deleteAsset(id);
        refreshCache();
    }

    // ─── UPDATE PRICE ──────────────────────────────────────────────────────────

    public void updateCurrentPrice(int assetId, double price) throws SQLException, AssetNotFoundException {
        assetDAO.getAssetById(assetId); // Verify exists
        assetDAO.savePriceEntry(assetId, price);
        currentPrices.put(assetId, price);
    }

    // ─── VIEW ALL ASSETS ───────────────────────────────────────────────────────

    public List<Asset> getAllAssets() {
        return Collections.unmodifiableList(cachedAssets);
    }

    // ─── SORT using Collections ─────────────────────────────────────────────────

    // Sort by P&L descending (best performers first) - Demonstrates Comparator
    public List<Asset> getSortedByProfitLoss() {
        List<Asset> sorted = new ArrayList<>(cachedAssets);
        sorted.sort((a, b) -> {
            double pnlA = a.getProfitLoss(currentPrices.getOrDefault(a.getId(), a.getBuyPrice()));
            double pnlB = b.getProfitLoss(currentPrices.getOrDefault(b.getId(), b.getBuyPrice()));
            return Double.compare(pnlB, pnlA);
        });
        return sorted;
    }

    // Sort by total invested descending
    public List<Asset> getSortedByInvestment() {
        List<Asset> sorted = new ArrayList<>(cachedAssets);
        sorted.sort(Comparator.comparingDouble(Asset::getTotalInvestment).reversed());
        return sorted;
    }

    // ─── GROUP BY TYPE using HashMap ───────────────────────────────────────────

    public Map<String, List<Asset>> getAssetsByType() {
        // Demonstrates: HashMap grouping, Collections Framework
        Map<String, List<Asset>> grouped = new HashMap<>();
        for (Asset asset : cachedAssets) {
            grouped.computeIfAbsent(asset.getAssetType(), k -> new ArrayList<>()).add(asset);
        }
        return grouped;
    }

    // ─── PORTFOLIO SUMMARY ─────────────────────────────────────────────────────

    public PortfolioSummary getPortfolioSummary() {
        PortfolioSummary summary = new PortfolioSummary();
        for (Asset asset : cachedAssets) {
            double price = currentPrices.getOrDefault(asset.getId(), asset.getBuyPrice());
            summary.addAsset(asset, price);
        }
        return summary;
    }

    // ─── SEARCH ────────────────────────────────────────────────────────────────

    public List<Asset> searchBySymbolOrName(String query) {
        String lowerQuery = query.toLowerCase();
        // Demonstrates: List iteration, String methods
        return cachedAssets.stream()
                .filter(a -> a.getSymbol().toLowerCase().contains(lowerQuery) ||
                             a.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    // ─── PRICE LOOKUP ──────────────────────────────────────────────────────────

    public double getCurrentPrice(int assetId) {
        // If no price set, fallback to buy price
        Asset asset = cachedAssets.stream()
                .filter(a -> a.getId() == assetId)
                .findFirst().orElse(null);
        if (asset == null) return 0;
        return currentPrices.getOrDefault(assetId, asset.getBuyPrice());
    }

    public Map<Integer, Double> getAllCurrentPrices() {
        return Collections.unmodifiableMap(currentPrices);
    }

    // ─── EXPORT ────────────────────────────────────────────────────────────────

    public String exportCSV() throws IOException {
        return ReportWriter.exportToCSV(cachedAssets, currentPrices);
    }

    public String exportTXT() throws IOException {
        PortfolioSummary summary = getPortfolioSummary();
        return ReportWriter.exportToTXT(cachedAssets, currentPrices, summary);
    }
}
