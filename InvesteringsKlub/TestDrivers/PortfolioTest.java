import Builder.PortfolioBuilder;
import CSVHandler.CSVStockRepository;
import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import Domain.Stock;


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
    void testBuyStockUpdatesPortfolio() {
        boolean result = portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        assertTrue(result);

        // Holdings
        Holding holding = portfolio.getHoldings().get("AAPL");
        double expectedValue = holding.getQuantity() * holding.getCurrentPriceDKK();
        assertNotNull(holding);
        assertEquals(10, holding.getQuantity());
        assertEquals(150.0, holding.getPurchasePriceDKK());

        // Cash balance
        assertEquals(10000.0 - 10 * 150.0, portfolio.getCashBalance());

        // Total value
        portfolio.updateTotalValue(stockRepo);
        assertEquals(10 * 150.0, portfolio.getTotalValueDKK());
    }

    @Test
    void testSellStockUpdatesPortfolio() {
        portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        boolean result = portfolio.sellStock("AAPL", 5, stockRepo, transactionRepo);
        assertTrue(result);

        Holding holding = portfolio.getHoldings().get("AAPL");
        assertNotNull(holding);
        assertEquals(5, holding.getQuantity());

        assertEquals(10000.0 - 1500.0 + 750.0, portfolio.getCashBalance());

        portfolio.updateTotalValue(stockRepo);
        double expectedTotalValue = holding.getQuantity() * holding.getCurrentPriceDKK();
        assertEquals(expectedTotalValue, portfolio.getTotalValueDKK());
    }

    @Test
    void testBuyStockFailsWithInsufficientFunds() {
        boolean result = portfolio.buyStock("GOOG", 10, stockRepo, transactionRepo); //2800*10 > 10000
        assertFalse(result);

        assertNull(portfolio.getHoldings().get("GOOG"));
        assertEquals(10000.0, portfolio.getCashBalance());
    }

    @Test
    void testSellStockFailsWithTooManyShares() {
        portfolio.buyStock("AAPL", 3, stockRepo, transactionRepo);

        boolean result = portfolio.sellStock("AAPL", 5, stockRepo, transactionRepo);
        assertFalse(result);

        assertEquals(3, portfolio.getHoldings().get("AAPL").getQuantity());
        assertEquals(10000.0 - 450.0, portfolio.getCashBalance());
    }

    @Test
    void testSeeStockMarketLoadsFromCSV() {
        CSVStockRepository repo = new CSVStockRepository();
        repo.loadFromCSV("InvesteringsKlub/CSVRepository/stockMarket.csv");
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
        // Køb 10 AAPL til 150 (fra @BeforeEach-setup)
        boolean buyResult = portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        assertTrue(buyResult);

        // Tjek at vi har korrekt holding
        Holding holding = portfolio.getHoldings().get("AAPL");
        assertNotNull(holding);
        assertEquals(10, holding.getQuantity());
        assertEquals(150.0, holding.getPurchasePriceDKK(), 0.001);

        // Simulér kursstigning: AAPL går fra 150 -> 200
        Stock aapl = stockRepo.getStockByTicker("AAPL");
        aapl.setPrice(200.0); // bruger setPrice fra TradableItem

        // ACT – brug de nye beregningsmetoder
        double investedDKK   = portfolio.calculateTotalInvestedDKK();
        double currentDKK    = portfolio.calculateCurrentHoldingsValueDKK(stockRepo);
        double realReturnDKK = portfolio.calculateRealReturnDKK(stockRepo);
        double percentReturn = portfolio.calculateReturnPercentage(stockRepo);

        // ASSERT
        // Investering: 10 * 150 = 1500
        assertEquals(1500.0, investedDKK, 0.001);

        // Nuværdi: 10 * 200 = 2000
        assertEquals(2000.0, currentDKK, 0.001);

        // Reelt afkast: 2000 - 1500 = 500
        assertEquals(500.0, realReturnDKK, 0.001);

        // Procentafkast: (500 / 1500) * 100 = 33,33...%
        double expectedPercent = (500.0 / 1500.0) * 100.0;
        assertEquals(expectedPercent, percentReturn, 0.001);
    }
 // Skal gemmes i holdings.csv.



}
