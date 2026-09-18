package com.portfolio.service;

import com.portfolio.exception.AssetNotFoundException;
import com.portfolio.exception.InvalidInputException;
import com.portfolio.model.Asset;
import com.portfolio.model.PortfolioSummary;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PortfolioService Integration & Logic Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PortfolioServiceTest {

    private static PortfolioService portfolioService;

    @BeforeAll
    static void setUp() {
        portfolioService = new PortfolioService();
    }

    @Test
    @Order(1)
    @DisplayName("Add Stock and verify caching")
    void testAddStock() throws InvalidInputException, SQLException {
        Asset stock = portfolioService.addStock("TEST_TCS", "Tata Consultancy Services",
                "10", "3000", "2024-01-01", "INR", "NSE", "IT");

        assertNotNull(stock);
        assertTrue(stock.getId() > 0);
        assertEquals("TEST_TCS", stock.getSymbol());

        List<Asset> searchResults = portfolioService.searchBySymbolOrName("TEST_TCS");
        assertFalse(searchResults.isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Add Crypto and verify searching")
    void testAddCrypto() throws InvalidInputException, SQLException {
        Asset crypto = portfolioService.addCrypto("TEST_BTC", "Bitcoin Test",
                "0.5", "2000000", "2024-02-01", "INR", "Bitcoin", "0xabc");

        assertNotNull(crypto);
        assertEquals("TEST_BTC", crypto.getSymbol());

        List<Asset> searchResults = portfolioService.searchBySymbolOrName("Bitcoin Test");
        assertFalse(searchResults.isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("Add Mutual Fund and verify grouping by type")
    void testAddMutualFundAndGrouping() throws InvalidInputException, SQLException {
        portfolioService.addMutualFund("TEST_HDFC", "HDFC Top 100 Test",
                "50", "100", "2024-03-01", "INR", "HDFC", "Equity", "1.5");

        Map<String, List<Asset>> grouped = portfolioService.getAssetsByType();
        assertTrue(grouped.containsKey("STOCK"));
        assertTrue(grouped.containsKey("CRYPTO"));
        assertTrue(grouped.containsKey("MUTUAL_FUND"));
    }

    @Test
    @Order(4)
    @DisplayName("Update current prices and check sorting by Profit/Loss")
    void testUpdatePriceAndSorting() throws SQLException, AssetNotFoundException {
        List<Asset> all = portfolioService.getAllAssets();
        assertFalse(all.isEmpty());

        Asset firstAsset = all.get(0);
        double newPrice = firstAsset.getBuyPrice() * 1.5; // 50% profit
        portfolioService.updateCurrentPrice(firstAsset.getId(), newPrice);

        assertEquals(newPrice, portfolioService.getCurrentPrice(firstAsset.getId()), 0.001);

        List<Asset> sorted = portfolioService.getSortedByProfitLoss();
        assertNotNull(sorted);
        assertFalse(sorted.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Portfolio Summary generates valid aggregate calculations")
    void testPortfolioSummary() {
        PortfolioSummary summary = portfolioService.getPortfolioSummary();
        assertNotNull(summary);
        assertTrue(summary.getTotalAssets() >= 3);
        assertTrue(summary.getTotalInvested() > 0);
    }
}
