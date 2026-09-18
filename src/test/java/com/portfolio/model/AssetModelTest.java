package com.portfolio.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Asset Domain Model Unit Tests")
public class AssetModelTest {

    @Test
    @DisplayName("Stock - Verify properties, polymorphism and P&L calculations")
    void testStockModel() {
        Stock stock = new Stock(1, "tcs", "Tata Consultancy Services", 10, 3500.0,
                LocalDate.of(2024, 1, 15), "INR", "NSE", "IT");

        assertEquals(1, stock.getId());
        assertEquals("TCS", stock.getSymbol(), "Symbol must be normalized to uppercase");
        assertEquals("Tata Consultancy Services", stock.getName());
        assertEquals("STOCK", stock.getAssetType());
        assertEquals("NSE", stock.getExchange());
        assertEquals("IT", stock.getSector());

        // Total investment: 10 * 3500 = 35,000
        assertEquals(35000.0, stock.getTotalInvestment(), 0.001);

        // Price goes up to 4000: Current value = 40,000
        assertEquals(40000.0, stock.calculateCurrentValue(4000.0), 0.001);
        assertEquals(5000.0, stock.getProfitLoss(4000.0), 0.001);
        assertEquals(14.2857, stock.getProfitLossPercent(4000.0), 0.01);

        // Price drops to 3000: Current value = 30,000
        assertEquals(30000.0, stock.calculateCurrentValue(3000.0), 0.001);
        assertEquals(-5000.0, stock.getProfitLoss(3000.0), 0.001);
        assertEquals(-14.2857, stock.getProfitLossPercent(3000.0), 0.01);

        assertTrue(stock.getSummary().contains("Stock: Tata Consultancy Services"));
    }

    @Test
    @DisplayName("Crypto - Verify fractional holdings and calculations")
    void testCryptoModel() {
        Crypto crypto = new Crypto(2, "btc", "Bitcoin", 0.5, 3000000.0,
                LocalDate.of(2024, 2, 10), "INR", "Bitcoin", "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh");

        assertEquals("BTC", crypto.getSymbol());
        assertEquals("CRYPTO", crypto.getAssetType());
        assertEquals("Bitcoin", crypto.getBlockchain());
        assertEquals("bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh", crypto.getWalletAddress());

        // Total investment: 0.5 * 3,000,000 = 1,500,000
        assertEquals(1500000.0, crypto.getTotalInvestment(), 0.001);

        // Market price rises to 4,000,000: Current value = 2,000,000
        assertEquals(2000000.0, crypto.calculateCurrentValue(4000000.0), 0.001);
        assertEquals(500000.0, crypto.getProfitLoss(4000000.0), 0.001);
        assertEquals(33.333, crypto.getProfitLossPercent(4000000.0), 0.01);

        assertTrue(crypto.getSummary().contains("Holdings: 0.50000000 coins"));
    }

    @Test
    @DisplayName("MutualFund - Verify NAV and expense ratio")
    void testMutualFundModel() {
        MutualFund mf = new MutualFund(3, "hdfc-top100", "HDFC Top 100", 200, 50.0,
                LocalDate.of(2024, 1, 1), "INR", "HDFC AMC", "Large Cap", 1.25);

        assertEquals("HDFC-TOP100", mf.getSymbol());
        assertEquals("MUTUAL_FUND", mf.getAssetType());
        assertEquals("HDFC AMC", mf.getFundHouse());
        assertEquals("Large Cap", mf.getCategory());
        assertEquals(1.25, mf.getExpenseRatio(), 0.001);

        // Total investment: 200 * 50 = 10,000
        assertEquals(10000.0, mf.getTotalInvestment(), 0.001);

        // Current NAV rises to 60: Current value = 12,000
        assertEquals(12000.0, mf.calculateCurrentValue(60.0), 0.001);
        assertEquals(2000.0, mf.getProfitLoss(60.0), 0.001);
        assertEquals(20.0, mf.getProfitLossPercent(60.0), 0.001);

        assertTrue(mf.getSummary().contains("Expense Ratio: 1.25%"));
    }
}
