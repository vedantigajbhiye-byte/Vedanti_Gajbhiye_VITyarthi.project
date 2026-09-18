package com.portfolio.util;

import com.portfolio.model.Asset;
import com.portfolio.model.PortfolioSummary;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Handles exporting portfolio data to files.
 * Demonstrates: Java I/O Streams, FileWriter, BufferedWriter,
 *               FileOutputStream, InputStreamReader (Unit 4)
 */
public class ReportWriter {

    private static final String REPORTS_DIR = "reports";

    // Demonstrates: Character-oriented streams (FileWriter, BufferedWriter)
    public static String exportToCSV(List<Asset> assets, Map<Integer, Double> currentPrices) throws IOException {
        ensureReportsDirectory();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = REPORTS_DIR + File.separator + "portfolio_" + timestamp + ".csv";

        // Using BufferedWriter (Character-oriented stream)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            // Write CSV header
            writer.write("ID,Symbol,Name,Type,Quantity,Buy Price,Current Price,Invested,Current Value,P&L,Return%,Buy Date,Currency");
            writer.newLine();

            for (Asset asset : assets) {
                double currentPrice = currentPrices.getOrDefault(asset.getId(), asset.getBuyPrice());
                double invested = asset.getTotalInvestment();
                double currentValue = asset.calculateCurrentValue(currentPrice);
                double pnl = asset.getProfitLoss(currentPrice);
                double returnPct = asset.getProfitLossPercent(currentPrice);

                writer.write(String.format("%d,%s,%s,%s,%.4f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%%,%s,%s",
                        asset.getId(),
                        asset.getSymbol(),
                        asset.getName().replace(",", ";"),  // Escape commas
                        asset.getAssetType(),
                        asset.getQuantity(),
                        asset.getBuyPrice(),
                        currentPrice,
                        invested,
                        currentValue,
                        pnl,
                        returnPct,
                        asset.getBuyDate().toString(),
                        asset.getCurrency()
                ));
                writer.newLine();
            }
        }

        return filename;
    }

    // Demonstrates: Byte-oriented streams (FileOutputStream) + text report
    public static String exportToTXT(List<Asset> assets, Map<Integer, Double> currentPrices,
                                      PortfolioSummary summary) throws IOException {
        ensureReportsDirectory();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = REPORTS_DIR + File.separator + "portfolio_report_" + timestamp + ".txt";

        // Using FileOutputStream (Byte-oriented stream) wrapped for text
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {

            writer.println("╔══════════════════════════════════════════════════════════╗");
            writer.println("║         PERSONAL PORTFOLIO TRACKER - REPORT              ║");
            writer.println("╚══════════════════════════════════════════════════════════╝");
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
            writer.println("=".repeat(62));
            writer.println();

            // Summary section
            writer.println("PORTFOLIO SUMMARY");
            writer.println("-".repeat(62));
            writer.printf("%-30s: ₹ %,.2f%n", "Total Invested", summary.getTotalInvested());
            writer.printf("%-30s: ₹ %,.2f%n", "Current Value", summary.getTotalCurrentValue());
            writer.printf("%-30s: ₹ %,.2f%n", "Total P&L", summary.getTotalProfitLoss());
            writer.printf("%-30s: %.2f%%%n", "Overall Return", summary.getOverallReturnPercent());
            writer.printf("%-30s: %d%n", "Total Assets", summary.getTotalAssets());
            writer.printf("  %-28s: %d%n", "Stocks", summary.getStockCount());
            writer.printf("  %-28s: %d%n", "Crypto", summary.getCryptoCount());
            writer.printf("  %-28s: %d%n", "Mutual Funds", summary.getMutualFundCount());
            writer.println();

            // Asset details
            writer.println("ASSET DETAILS");
            writer.println("-".repeat(62));
            for (Asset asset : assets) {
                double currentPrice = currentPrices.getOrDefault(asset.getId(), asset.getBuyPrice());
                double pnl = asset.getProfitLoss(currentPrice);
                String trend = pnl >= 0 ? "▲" : "▼";

                writer.println();
                writer.printf("  %s  [%s] %s (%s)%n", trend, asset.getAssetType(), asset.getName(), asset.getSymbol());
                writer.printf("     %-22s: %.4f%n", "Quantity", asset.getQuantity());
                writer.printf("     %-22s: %.2f%n", "Buy Price", asset.getBuyPrice());
                writer.printf("     %-22s: %.2f%n", "Current Price", currentPrice);
                writer.printf("     %-22s: ₹ %,.2f%n", "Invested", asset.getTotalInvestment());
                writer.printf("     %-22s: ₹ %,.2f%n", "Current Value", asset.calculateCurrentValue(currentPrice));
                writer.printf("     %-22s: ₹ %,.2f (%.2f%%)%n", "P&L", pnl, asset.getProfitLossPercent(currentPrice));
                writer.printf("     %-22s: %s%n", "Buy Date", asset.getBuyDate());
                writer.println("     " + "-".repeat(40));
            }

            writer.println();
            writer.println("═".repeat(62));
            writer.println("         End of Report - Portfolio Tracker v1.0");
            writer.println("═".repeat(62));
        }

        return filename;
    }

    // Read back a previously saved report - demonstrates FileReader / BufferedReader
    public static String readReport(String filename) throws IOException {
        StringBuilder content = new StringBuilder();

        // Using BufferedReader (Character-oriented input stream)
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static void ensureReportsDirectory() throws IOException {
        File dir = new File(REPORTS_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create reports directory: " + REPORTS_DIR);
        }
    }
}
