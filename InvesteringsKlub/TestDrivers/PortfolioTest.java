
import CSVHandler.CSVStockRepository;
import CSVHandler.StockRepository;
import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {
    private InMemoryStockRepository stockRepo;
    private InMemoryTransactionRepository transactionRepo;
    private User user;
    private Portfolio portfolio;

    @BeforeEach
    void setup() {
        stockRepo = new InMemoryStockRepository();
        stockRepo.addStock(new Stock("AAPL", 150.0, "DKK", "Apple Inc.", "Tech"));
        stockRepo.addStock(new Stock("GOOG", 2800.0, "DKK", "Google Inc.", "Tech"));

        transactionRepo = new InMemoryTransactionRepository();

        user = new User(
                1,
                "Alice Johnson",
                "alice@example.com",
                new Date(1990, 1, 1),
                10000,
                new Date(),
                new Date()
        );
        portfolio = user.getPortfolio();
    }

    @Test
    void testBuyStockUpdatesPortfolioandTotalIncludesCash() {
        boolean result = portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        assertTrue(result);

        Holding holding = portfolio.get("AAPL");
        assertNotNull(holding);


        assertEquals(10, holding.getQuantity());
        assertEquals(150.0, holding.getPurchasePriceDKK(), 0.0001);

        // Cash decreased by the purchase amount
        assertEquals(10000.0 - 10 * 150.0, portfolio.getCashBalance(), 0.0001);

        // Total value includes cash (with unchanged market price the total stays 10,000)
        portfolio.updateTotalValue(stockRepo);
        assertEquals(10000.0, portfolio.getTotalValueDKK(), 0.0001);
    }

    @Test
    void testSellStockUpdatesPortfolio_totalIncludesCash() {
        portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        boolean result = portfolio.sellStock("AAPL", 5, stockRepo, transactionRepo);
        assertTrue(result);

        Holding holding = portfolio.get("AAPL");
        assertNotNull(holding);
        assertEquals(5, holding.getQuantity());

        // Cash after buy (10000 - 1500) then sell (+750) = 9250
        assertEquals(10000.0 - 1500.0 + 750.0, portfolio.getCashBalance(), 0.0001);

        // Total value = holdings (5 * 150) + cash (9250) = 10,000 (unchanged price)
        portfolio.updateTotalValue(stockRepo);
        assertEquals(10000.0, portfolio.getTotalValueDKK(), 0.0001);
    }

    @Test
    void testBuyStockFailsWithInsufficientFunds() {
        boolean result = portfolio.buyStock("GOOG", 10, stockRepo, transactionRepo); // 2800 * 10 > 10000
        assertFalse(result);
        assertNull(portfolio.get("GOOG"));
        assertEquals(10000.0, portfolio.getCashBalance(), 0.0001);

        portfolio.updateTotalValue(stockRepo);
        assertEquals(10000.0, portfolio.getTotalValueDKK(), 0.0001);
    }

    @Test
    void testSellStockFailsWithTooManyShares() {
        portfolio.buyStock("AAPL", 3, stockRepo, transactionRepo);
        boolean result = portfolio.sellStock("AAPL", 5, stockRepo, transactionRepo);
        assertFalse(result);

        assertEquals(3, portfolio.get("AAPL").getQuantity());
        assertEquals(10000.0 - 450.0, portfolio.getCashBalance(), 0.0001);

        portfolio.updateTotalValue(stockRepo);
        assertEquals((3 * 150.0) + (10000.0 - 450.0), portfolio.getTotalValueDKK(), 0.0001);
    }

    @Test
    void testSeeStockMarketLoadsFromCSV() {
        StockRepository repo = new CSVStockRepository();
        List<Stock> stocks = repo.getAllStocks();
        assertFalse(stocks.isEmpty(), "Listen over aktier må ikke være tom");

        Stock pandora = repo.getStockByTicker("Pandora");
        assertNotNull(pandora, "PANDORA burde være i listen");
        assertEquals("Pandora", pandora.getName());
        assertEquals(765.0, pandora.getPrice(), 0.0001);
        assertEquals("DKK", pandora.getCurrency());
    }

    @Test
    void testPortfolioReturnCalculations() {
        // Buy 10 AAPL at 150
        boolean buyResult = portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        assertTrue(buyResult);

        Holding holding = portfolio.get("AAPL");
        assertNotNull(holding);
        assertEquals(10, holding.getQuantity());
        assertEquals(150.0, holding.getPurchasePriceDKK(), 0.001);

        // Simulate price increase: 150 -> 200
        Stock aapl = stockRepo.getStockByTicker("AAPL");
        aapl.setPrice(200.0);

        // Invested is holdings-only at cost
        double investedDKK = portfolio.calculateTotalInvestedDKK();
        assertEquals(1500.0, investedDKK, 0.001);

        // "Including cash" should truly include cash: 10 * 200 + (10000 - 1500) = 10500
        double currentDKK = portfolio.calculatePortfolioValueIncludingCashDKK(stockRepo);
        assertEquals(2000.0 + (10000.0 - 1500.0), currentDKK, 0.001);

        // Real return and percentage are based on holdings value vs invested
        double realReturnDKK = portfolio.calculateRealReturnDKK(stockRepo);
        double percentReturn = portfolio.calculateReturnPercentage(stockRepo);
        assertEquals(500.0, realReturnDKK, 0.001);
        double expectedPercent = (500.0 / 1500.0) * 100.0;
        assertEquals(expectedPercent, percentReturn, 0.001);
    }
}
