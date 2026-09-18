package com.portfolio.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PortfolioSummary Unit Tests")
public class PortfolioSummaryTest {

    @Test
    @DisplayName("Verify aggregation across multiple asset types")
    void testPortfolioSummaryAggregation() {
        PortfolioSummary summary = new PortfolioSummary();

        assertEquals(0, summary.getTotalAssets());
        assertEquals(0.0, summary.getTotalInvested());
        assertEquals(0.0, summary.getTotalCurrentValue());

        // Stock: Invested 10 * 1000 = 10,000; Current price 1200 -> Value 12,000
        Stock stock = new Stock(1, "INFY", "Infosys", 10, 1000.0, LocalDate.now(), "INR", "NSE", "IT");
        summary.addAsset(stock, 1200.0);

        // Crypto: Invested 1 * 50,000 = 50,000; Current price 40,000 -> Value 40,000
        Crypto crypto = new Crypto(2, "ETH", "Ethereum", 1, 50000.0, LocalDate.now(), "INR", "Ethereum", "0x123");
        summary.addAsset(crypto, 40000.0);

        // Mutual Fund: Invested 100 * 100 = 10,000; Current NAV 110 -> Value 11,000
        MutualFund mf = new MutualFund(3, "SBI-BLUE", "SBI Bluechip", 100, 100.0, LocalDate.now(), "INR", "SBI", "Large Cap", 1.0);
        summary.addAsset(mf, 110.0);

        // Totals:
        // Total invested = 10000 + 50000 + 10000 = 70,000
        // Total current value = 12000 + 40000 + 11000 = 63,000
        // Total P&L = 63000 - 70000 = -7000
        // Return % = (-7000 / 70000) * 100 = -10.0%

        assertEquals(3, summary.getTotalAssets());
        assertEquals(1, summary.getStockCount());
        assertEquals(1, summary.getCryptoCount());
        assertEquals(1, summary.getMutualFundCount());

        assertEquals(70000.0, summary.getTotalInvested(), 0.001);
        assertEquals(63000.0, summary.getTotalCurrentValue(), 0.001);
        assertEquals(-7000.0, summary.getTotalProfitLoss(), 0.001);
        assertEquals(-10.0, summary.getOverallReturnPercent(), 0.001);
    }
}
