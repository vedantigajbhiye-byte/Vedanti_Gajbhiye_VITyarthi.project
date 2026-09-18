package com.portfolio;

import com.portfolio.exception.AssetNotFoundException;
import com.portfolio.exception.InvalidInputException;
import com.portfolio.model.*;
import com.portfolio.service.PortfolioService;
import com.portfolio.util.DatabaseManager;
import com.portfolio.util.InputValidator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application entry point.
 * Demonstrates: Java I/O (Scanner), Flow Control, Exception Handling,
 *               all topics from Units 1-5.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static PortfolioService portfolioService;

    // ANSI color codes for styled CLI output
    private static final String RESET  = "\033[0m";
    private static final String BOLD   = "\033[1m";
    private static final String GREEN  = "\033[32m";
    private static final String RED    = "\033[31m";
    private static final String CYAN   = "\033[36m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE   = "\033[34m";
    private static final String WHITE  = "\033[37m";

    public static void main(String[] args) {
        printBanner();
        System.out.println(CYAN + "Initializing Portfolio Tracker..." + RESET);

        try {
            portfolioService = new PortfolioService();
        } catch (Exception e) {
            System.err.println(RED + "Failed to initialize: " + e.getMessage() + RESET);
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            try {
                printMainMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1" -> addAssetMenu();
                    case "2" -> viewAllAssets();
                    case "3" -> viewPortfolioSummary();
                    case "4" -> updatePriceMenu();
                    case "5" -> deleteAssetMenu();
                    case "6" -> searchAssets();
                    case "7" -> viewSortedAssets();
                    case "8" -> viewByType();
                    case "9" -> exportMenu();
                    case "0" -> {
                        running = false;
                        System.out.println(CYAN + "\nThank you for using Portfolio Tracker. Goodbye!" + RESET);
                    }
                    default -> System.out.println(YELLOW + "Invalid option. Please enter 0-9." + RESET);
                }

            } catch (Exception e) {
                System.err.println(RED + "\n[ERROR] " + e.getMessage() + RESET);
            }
        }

        DatabaseManager.getInstance().close();
        scanner.close();
    }

    // ─── MENUS ─────────────────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║        💼  PERSONAL PORTFOLIO TRACKER  v1.0              ║");
        System.out.println("║        Track Stocks • Crypto • Mutual Funds              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println(BOLD + WHITE + "═══════════════ MAIN MENU ═══════════════" + RESET);
        System.out.println("  " + CYAN + "1" + RESET + ". ➕  Add New Asset");
        System.out.println("  " + CYAN + "2" + RESET + ". 📋  View All Assets");
        System.out.println("  " + CYAN + "3" + RESET + ". 📊  Portfolio Summary");
        System.out.println("  " + CYAN + "4" + RESET + ". 💲  Update Current Price");
        System.out.println("  " + CYAN + "5" + RESET + ". 🗑️   Delete Asset");
        System.out.println("  " + CYAN + "6" + RESET + ". 🔍  Search Assets");
        System.out.println("  " + CYAN + "7" + RESET + ". 📈  View Sorted by P&L");
        System.out.println("  " + CYAN + "8" + RESET + ". 🗂️   View by Asset Type");
        System.out.println("  " + CYAN + "9" + RESET + ". 💾  Export Reports");
        System.out.println("  " + RED  + "0" + RESET + ". 🚪  Exit");
        System.out.println(BOLD + WHITE + "═════════════════════════════════════════" + RESET);
        System.out.print("Enter your choice: ");
    }

    // ─── ADD ASSET ─────────────────────────────────────────────────────────────

    private static void addAssetMenu() throws InvalidInputException, SQLException {
        System.out.println(BOLD + "\n─── Add New Asset ───" + RESET);
        System.out.println("  1. Stock");
        System.out.println("  2. Cryptocurrency");
        System.out.println("  3. Mutual Fund");
        System.out.print("Choose type: ");
        String type = scanner.nextLine().trim();

        switch (type) {
            case "1" -> addStockFlow();
            case "2" -> addCryptoFlow();
            case "3" -> addMutualFundFlow();
            default -> System.out.println(YELLOW + "Invalid type." + RESET);
        }
    }

    private static void addStockFlow() throws InvalidInputException, SQLException {
        System.out.println(BOLD + "\n── Add Stock ──" + RESET);
        String symbol   = prompt("Symbol (e.g. RELIANCE, TCS): ");
        String name     = prompt("Company Name: ");
        String quantity = prompt("Number of Shares: ");
        String price    = prompt("Buy Price per Share (₹): ");
        String date     = prompt("Buy Date (YYYY-MM-DD): ");
        String exchange = prompt("Exchange (NSE/BSE/NYSE) [default: NSE]: ");
        String sector   = prompt("Sector (e.g. Technology) [default: General]: ");

        if (exchange.isEmpty()) exchange = "NSE";
        if (sector.isEmpty()) sector = "General";

        Asset asset = portfolioService.addStock(symbol, name, quantity, price, date, "INR", exchange, sector);
        System.out.println(GREEN + "\n✅ Stock added successfully! ID: " + asset.getId() + RESET);
        System.out.println("   " + asset);
    }

    private static void addCryptoFlow() throws InvalidInputException, SQLException {
        System.out.println(BOLD + "\n── Add Cryptocurrency ──" + RESET);
        String symbol     = prompt("Symbol (e.g. BTC, ETH): ");
        String name       = prompt("Coin Name (e.g. Bitcoin): ");
        String quantity   = prompt("Quantity (coins): ");
        String price      = prompt("Buy Price per Coin (₹): ");
        String date       = prompt("Buy Date (YYYY-MM-DD): ");
        String blockchain = prompt("Blockchain (e.g. Bitcoin, Ethereum) [default: Unknown]: ");
        String wallet     = prompt("Wallet Address [optional, press Enter to skip]: ");

        if (blockchain.isEmpty()) blockchain = "Unknown";
        if (wallet.isEmpty()) wallet = "N/A";

        Asset asset = portfolioService.addCrypto(symbol, name, quantity, price, date, "INR", blockchain, wallet);
        System.out.println(GREEN + "\n✅ Crypto asset added! ID: " + asset.getId() + RESET);
        System.out.println("   " + asset);
    }

    private static void addMutualFundFlow() throws InvalidInputException, SQLException {
        System.out.println(BOLD + "\n── Add Mutual Fund ──" + RESET);
        String symbol    = prompt("Fund Code/Symbol: ");
        String name      = prompt("Fund Name: ");
        String units     = prompt("Units Purchased: ");
        String nav       = prompt("NAV at purchase (₹): ");
        String date      = prompt("Purchase Date (YYYY-MM-DD): ");
        String fundHouse = prompt("Fund House (e.g. SBI, HDFC): ");
        String category  = prompt("Category (e.g. Large Cap, Debt): ");
        String expense   = prompt("Expense Ratio (%) [e.g. 1.5]: ");

        Asset asset = portfolioService.addMutualFund(symbol, name, units, nav, date, "INR",
                fundHouse, category, expense);
        System.out.println(GREEN + "\n✅ Mutual Fund added! ID: " + asset.getId() + RESET);
        System.out.println("   " + asset);
    }

    // ─── VIEW ALL ──────────────────────────────────────────────────────────────

    private static void viewAllAssets() {
        List<Asset> assets = portfolioService.getAllAssets();
        if (assets.isEmpty()) {
            System.out.println(YELLOW + "\nNo assets in portfolio yet. Add some first!" + RESET);
            return;
        }

        System.out.println(BOLD + "\n📋 ALL ASSETS (" + assets.size() + " total)" + RESET);
        printAssetTableHeader();

        Map<Integer, Double> prices = portfolioService.getAllCurrentPrices();
        for (Asset asset : assets) {
            double currentPrice = prices.getOrDefault(asset.getId(), asset.getBuyPrice());
            printAssetRow(asset, currentPrice);
        }
        printTableFooter();
    }

    // ─── PORTFOLIO SUMMARY ─────────────────────────────────────────────────────

    private static void viewPortfolioSummary() {
        PortfolioSummary summary = portfolioService.getPortfolioSummary();
        if (summary.getTotalAssets() == 0) {
            System.out.println(YELLOW + "\nNo assets to summarize." + RESET);
            return;
        }

        System.out.println(BOLD + "\n📊 PORTFOLIO SUMMARY" + RESET);
        System.out.println("─".repeat(50));
        System.out.printf("  %-28s: " + CYAN + "₹ %,.2f" + RESET + "%n", "Total Invested", summary.getTotalInvested());
        System.out.printf("  %-28s: " + CYAN + "₹ %,.2f" + RESET + "%n", "Current Value", summary.getTotalCurrentValue());

        String pnlColor = summary.getTotalProfitLoss() >= 0 ? GREEN : RED;
        String pnlSign  = summary.getTotalProfitLoss() >= 0 ? "+" : "";
        System.out.printf("  %-28s: " + pnlColor + pnlSign + "₹ %,.2f (%.2f%%)" + RESET + "%n",
                "Total P&L", summary.getTotalProfitLoss(), summary.getOverallReturnPercent());
        System.out.println("─".repeat(50));
        System.out.printf("  %-28s: %d%n", "Total Assets", summary.getTotalAssets());
        System.out.printf("  %-28s: %d%n", "  Stocks", summary.getStockCount());
        System.out.printf("  %-28s: %d%n", "  Crypto", summary.getCryptoCount());
        System.out.printf("  %-28s: %d%n", "  Mutual Funds", summary.getMutualFundCount());
        System.out.println("─".repeat(50));
    }

    // ─── UPDATE PRICE ──────────────────────────────────────────────────────────

    private static void updatePriceMenu() throws InvalidInputException, SQLException, AssetNotFoundException {
        System.out.println(BOLD + "\n─── Update Current Price ───" + RESET);
        viewAllAssets();
        String idStr    = prompt("\nEnter Asset ID to update price: ");
        String priceStr = prompt("Enter Current Market Price (₹): ");

        int id = InputValidator.validatePositiveInt(idStr, "Asset ID");
        double price = InputValidator.validatePositiveDouble(priceStr, "Market Price");

        portfolioService.updateCurrentPrice(id, price);
        System.out.println(GREEN + "✅ Price updated for Asset ID: " + id + RESET);
    }

    // ─── DELETE ────────────────────────────────────────────────────────────────

    private static void deleteAssetMenu() throws InvalidInputException, SQLException, AssetNotFoundException {
        System.out.println(BOLD + "\n─── Delete Asset ───" + RESET);
        viewAllAssets();
        String idStr = prompt("\nEnter Asset ID to delete: ");
        int id = InputValidator.validatePositiveInt(idStr, "Asset ID");

        System.out.print(RED + "Are you sure you want to delete Asset ID " + id + "? (yes/no): " + RESET);
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            portfolioService.deleteAsset(id);
            System.out.println(GREEN + "✅ Asset ID " + id + " deleted successfully." + RESET);
        } else {
            System.out.println(YELLOW + "Deletion cancelled." + RESET);
        }
    }

    // ─── SEARCH ────────────────────────────────────────────────────────────────

    private static void searchAssets() {
        String query = prompt("\n🔍 Search by symbol or name: ");
        List<Asset> results = portfolioService.searchBySymbolOrName(query);

        if (results.isEmpty()) {
            System.out.println(YELLOW + "No assets found matching '" + query + "'." + RESET);
            return;
        }

        System.out.println(BOLD + "\nSearch Results (" + results.size() + " found):" + RESET);
        printAssetTableHeader();
        Map<Integer, Double> prices = portfolioService.getAllCurrentPrices();
        for (Asset asset : results) {
            double currentPrice = prices.getOrDefault(asset.getId(), asset.getBuyPrice());
            printAssetRow(asset, currentPrice);
        }
        printTableFooter();
    }

    // ─── SORTED VIEW ───────────────────────────────────────────────────────────

    private static void viewSortedAssets() {
        System.out.println(BOLD + "\n📈 ASSETS SORTED BY P&L (Best → Worst)" + RESET);
        List<Asset> sorted = portfolioService.getSortedByProfitLoss();

        if (sorted.isEmpty()) {
            System.out.println(YELLOW + "No assets yet." + RESET);
            return;
        }

        printAssetTableHeader();
        Map<Integer, Double> prices = portfolioService.getAllCurrentPrices();
        for (Asset asset : sorted) {
            double currentPrice = prices.getOrDefault(asset.getId(), asset.getBuyPrice());
            printAssetRow(asset, currentPrice);
        }
        printTableFooter();
    }

    // ─── BY TYPE ───────────────────────────────────────────────────────────────

    private static void viewByType() {
        Map<String, List<Asset>> grouped = portfolioService.getAssetsByType();
        Map<Integer, Double> prices = portfolioService.getAllCurrentPrices();

        if (grouped.isEmpty()) {
            System.out.println(YELLOW + "\nNo assets in portfolio." + RESET);
            return;
        }

        for (Map.Entry<String, List<Asset>> entry : grouped.entrySet()) {
            System.out.println(BOLD + "\n🗂️  " + entry.getKey() + " (" + entry.getValue().size() + ")" + RESET);
            printAssetTableHeader();
            for (Asset asset : entry.getValue()) {
                double currentPrice = prices.getOrDefault(asset.getId(), asset.getBuyPrice());
                printAssetRow(asset, currentPrice);
            }
            printTableFooter();
        }
    }

    // ─── EXPORT ────────────────────────────────────────────────────────────────

    private static void exportMenu() throws IOException {
        System.out.println(BOLD + "\n─── Export Report ───" + RESET);
        System.out.println("  1. Export to CSV");
        System.out.println("  2. Export to TXT (Full Report)");
        System.out.println("  3. Both");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> {
                String csv = portfolioService.exportCSV();
                System.out.println(GREEN + "✅ CSV exported: " + csv + RESET);
            }
            case "2" -> {
                String txt = portfolioService.exportTXT();
                System.out.println(GREEN + "✅ TXT report exported: " + txt + RESET);
            }
            case "3" -> {
                String csv = portfolioService.exportCSV();
                String txt = portfolioService.exportTXT();
                System.out.println(GREEN + "✅ CSV:    " + csv + RESET);
                System.out.println(GREEN + "✅ Report: " + txt + RESET);
            }
            default -> System.out.println(YELLOW + "Invalid choice." + RESET);
        }
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    private static String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private static void printAssetTableHeader() {
        System.out.println();
        System.out.printf(BOLD + "  %-4s %-8s %-22s %-10s %-12s %-12s %-12s %-10s%n" + RESET,
                "ID", "TYPE", "NAME (SYMBOL)", "QTY", "BUY PRICE", "CUR PRICE", "P&L", "RETURN%");
        System.out.println("  " + "─".repeat(92));
    }

    private static void printAssetRow(Asset asset, double currentPrice) {
        double pnl = asset.getProfitLoss(currentPrice);
        double returnPct = asset.getProfitLossPercent(currentPrice);
        String pnlColor = pnl >= 0 ? GREEN : RED;
        String sign     = pnl >= 0 ? "+" : "";
        String nameSymbol = asset.getName() + " (" + asset.getSymbol() + ")";
        if (nameSymbol.length() > 22) nameSymbol = nameSymbol.substring(0, 19) + "...";

        System.out.printf("  %-4d %-8s %-22s %-10.4f %-12.2f %-12.2f " + pnlColor + "%-12.2f %-10.2f%%" + RESET + "%n",
                asset.getId(),
                asset.getAssetType(),
                nameSymbol,
                asset.getQuantity(),
                asset.getBuyPrice(),
                currentPrice,
                pnl,
                returnPct);
    }

    private static void printTableFooter() {
        System.out.println("  " + "─".repeat(92));
    }
}
