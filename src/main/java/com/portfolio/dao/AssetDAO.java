package com.portfolio.dao;

import com.portfolio.exception.AssetNotFoundException;
import com.portfolio.model.*;
import com.portfolio.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Asset CRUD operations.
 * Demonstrates: JDBC API - PreparedStatement, ResultSet,
 *               Submitting queries and getting results (Unit 5)
 */
public class AssetDAO {

    private final Connection connection;

    public AssetDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    // CREATE - Insert new asset
    public int addAsset(Asset asset) throws SQLException {
        String sql = "INSERT INTO assets (symbol, name, asset_type, quantity, buy_price, buy_date, currency, extra1, extra2, extra3) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, asset.getSymbol());
            pstmt.setString(2, asset.getName());
            pstmt.setString(3, asset.getAssetType());
            pstmt.setDouble(4, asset.getQuantity());
            pstmt.setDouble(5, asset.getBuyPrice());
            pstmt.setString(6, asset.getBuyDate().toString());
            pstmt.setString(7, asset.getCurrency());

            // Set type-specific extra fields
            if (asset instanceof Stock stock) {
                pstmt.setString(8, stock.getExchange());
                pstmt.setString(9, stock.getSector());
                pstmt.setNull(10, Types.VARCHAR);
            } else if (asset instanceof Crypto crypto) {
                pstmt.setString(8, crypto.getBlockchain());
                pstmt.setString(9, crypto.getWalletAddress());
                pstmt.setNull(10, Types.VARCHAR);
            } else if (asset instanceof MutualFund mf) {
                pstmt.setString(8, mf.getFundHouse());
                pstmt.setString(9, mf.getCategory());
                pstmt.setString(10, String.valueOf(mf.getExpenseRatio()));
            } else {
                pstmt.setNull(8, Types.VARCHAR);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.VARCHAR);
            }

            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                int generatedId = keys.getInt(1);
                asset.setId(generatedId);
                return generatedId;
            }
        }
        return -1;
    }

    // READ ALL - Get all assets
    public List<Asset> getAllAssets() throws SQLException {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM assets ORDER BY asset_type, name";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Asset asset = mapResultSetToAsset(rs);
                if (asset != null) assets.add(asset);
            }
        }
        return assets;
    }

    // READ BY ID - Get single asset
    public Asset getAssetById(int id) throws SQLException, AssetNotFoundException {
        String sql = "SELECT * FROM assets WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAsset(rs);
            } else {
                throw new AssetNotFoundException(id);
            }
        }
    }

    // READ BY TYPE
    public List<Asset> getAssetsByType(String assetType) throws SQLException {
        List<Asset> assets = new ArrayList<>();
        String sql = "SELECT * FROM assets WHERE asset_type = ? ORDER BY name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, assetType.toUpperCase());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Asset asset = mapResultSetToAsset(rs);
                if (asset != null) assets.add(asset);
            }
        }
        return assets;
    }

    // UPDATE - Update asset quantity and price
    public boolean updateAsset(int id, double newQuantity, double newBuyPrice) throws SQLException, AssetNotFoundException {
        // First check it exists
        getAssetById(id);

        String sql = "UPDATE assets SET quantity = ?, buy_price = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newQuantity);
            pstmt.setDouble(2, newBuyPrice);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - Remove an asset
    public boolean deleteAsset(int id) throws SQLException, AssetNotFoundException {
        getAssetById(id); // Throws if not found

        String sql = "DELETE FROM assets WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            // Also delete price history
            String deletePrices = "DELETE FROM price_history WHERE asset_id = ?";
            try (PreparedStatement pstmt2 = connection.prepareStatement(deletePrices)) {
                pstmt2.setInt(1, id);
                pstmt2.executeUpdate();
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    // Save current price for an asset
    public void savePriceEntry(int assetId, double price) throws SQLException {
        String sql = "INSERT INTO price_history (asset_id, price, recorded_date) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, assetId);
            pstmt.setDouble(2, price);
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.executeUpdate();
        }
    }

    // Get latest price for all assets
    public Map<Integer, Double> getLatestPrices() throws SQLException {
        Map<Integer, Double> prices = new HashMap<>();
        String sql = """
            SELECT asset_id, price FROM price_history
            WHERE id IN (
                SELECT MAX(id) FROM price_history GROUP BY asset_id
            )
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                prices.put(rs.getInt("asset_id"), rs.getDouble("price"));
            }
        }
        return prices;
    }

    // Map ResultSet row to Asset object (Factory-style)
    private Asset mapResultSetToAsset(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String symbol = rs.getString("symbol");
        String name = rs.getString("name");
        String type = rs.getString("asset_type");
        double quantity = rs.getDouble("quantity");
        double buyPrice = rs.getDouble("buy_price");
        LocalDate buyDate = LocalDate.parse(rs.getString("buy_date"));
        String currency = rs.getString("currency");
        String extra1 = rs.getString("extra1");
        String extra2 = rs.getString("extra2");
        String extra3 = rs.getString("extra3");

        return switch (type) {
            case "STOCK" -> new Stock(id, symbol, name, quantity, buyPrice, buyDate, currency,
                    extra1 != null ? extra1 : "NSE",
                    extra2 != null ? extra2 : "General");
            case "CRYPTO" -> new Crypto(id, symbol, name, quantity, buyPrice, buyDate, currency,
                    extra1 != null ? extra1 : "Unknown",
                    extra2 != null ? extra2 : "N/A");
            case "MUTUAL_FUND" -> new MutualFund(id, symbol, name, quantity, buyPrice, buyDate, currency,
                    extra1 != null ? extra1 : "Unknown",
                    extra2 != null ? extra2 : "General",
                    extra3 != null ? Double.parseDouble(extra3) : 0.0);
            default -> null;
        };
    }
}
