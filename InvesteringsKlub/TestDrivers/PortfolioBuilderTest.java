
import Builder.*;
import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioBuilderTest {

    private InMemoryStockRepository stockRepo;
    private InMemoryBondRepository bondRepo;
    private InMemoryTransactionRepository transactionRepo;
    private User user;
    private File tempCsvFile;

    @BeforeEach
    void setup() throws IOException {
        // Set up in-memory stocks
        stockRepo = new InMemoryStockRepository();
        stockRepo.addStock(new Stock("AAPL", 150.0, "DKK", "Apple Inc.", "Tech"));
        stockRepo.addStock(new Stock("GOOG", 2800.0, "DKK", "Google Inc.", "Tech"));
        // Set up in-memory bonds
        bondRepo = new InMemoryBondRepository();
        // In-memory transactions
        transactionRepo = new InMemoryTransactionRepository();

        // Test user
        user = new User(1, "Alice Johnson", "alice@example.com",
                new Date(90, Calendar.JANUARY, 1), 10000,
                new Date(), new Date());

        // Add initial transactions
        Date txDate = new Date();
        transactionRepo.writeTransaction(new Transaction(1, user.getUserId(), txDate, "AAPL", 150.0, "DKK", OrderType.BUY, 10));
        transactionRepo.writeTransaction(new Transaction(2, user.getUserId(), txDate, "AAPL", 150.0, "DKK", OrderType.SELL, 5));

        // Verify transactions exist
        assertEquals(2, transactionRepo.getTransactionsByUserId(user.getUserId()).size());

        // Temporary CSV file (not used by builder but kept for compatibility)
        tempCsvFile = File.createTempFile("transactions", ".csv");
        tempCsvFile.deleteOnExit();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            writer.write("userId,date,ticker,price,currency,orderType,quantity\n");
            writer.write("1,2025-11-30,AAPL,150.0,DKK,BUY,10\n");
            writer.write("1,2025-11-30,AAPL,150.0,DKK,SELL,5\n");
        }
    }

    @Test
    void testBuildPortfolioFromTransactions() {
        Portfolio portfolio = PortfolioBuilder.buildPortfolio(user, stockRepo, bondRepo, transactionRepo);
        user.printTransactionHistory(transactionRepo,1);
        double expectedCash = 10000.0 - (10 * 150.0) + (5 * 150.0);
        assertEquals(expectedCash, portfolio.getCashBalance());

        Holding aapl = portfolio.getHoldings().get("aapl");
        assertNotNull(aapl);
        assertEquals(5, aapl.getQuantity());
        assertEquals(150.0, aapl.getPurchasePriceDKK(), 0.01);

        portfolio.updateTotalValue(stockRepo, bondRepo);
        double expectedTotalValue = expectedCash + (5 * 150.0);
        assertEquals(expectedTotalValue, portfolio.getTotalValueDKK(), 0.01);

        assertEquals(2, transactionRepo.getAllTransactions().size());
    }

    @Test
    void testBuildPortfolioWithNoTransactions() {
        transactionRepo.clear(); // remove all transactions
        Portfolio portfolio = PortfolioBuilder.buildPortfolio(user, stockRepo, bondRepo, transactionRepo);

        assertEquals(user.getInitialCashDKK(), portfolio.getCashBalance(), 0.01);
        assertTrue(portfolio.getHoldings().isEmpty());
        assertEquals(user.getInitialCashDKK(), portfolio.getTotalValueDKK(), 0.01);
    }

    @Test
    void testBuildPortfolioWithMultipleBuys() {
        transactionRepo.clear();
        transactionRepo.writeTransaction(new Transaction(3, user.getUserId(), new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, 10));
        transactionRepo.writeTransaction(new Transaction(4, user.getUserId(), new Date(), "AAPL", 200.0, "DKK", OrderType.BUY, 10));

        Portfolio portfolio = PortfolioBuilder.buildPortfolio(user, stockRepo, bondRepo, transactionRepo);

        Holding aapl = portfolio.getHoldings().get("aapl");
        assertNotNull(aapl);
        assertEquals(20, aapl.getQuantity());
        double expectedAvgPrice = ((10 * 150.0) + (10 * 200.0)) / 20;
        assertEquals(expectedAvgPrice, aapl.getPurchasePriceDKK(), 0.01);
    }

    @Test
    void testBuildPortfolioSellMoreThanOwned() {
        transactionRepo.clear();
        transactionRepo.writeTransaction(new Transaction(5, user.getUserId(), new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, 5));
        transactionRepo.writeTransaction(new Transaction(6, user.getUserId(), new Date(), "AAPL", 150.0, "DKK", OrderType.SELL, 10)); // invalid sell

        Portfolio portfolio = PortfolioBuilder.buildPortfolio(user, stockRepo, bondRepo, transactionRepo);

        Holding aapl = portfolio.getHoldings().get("aapl");
        assertNotNull(aapl);
        assertEquals(5, aapl.getQuantity());
        assertTrue(portfolio.getCashBalance() <= user.getInitialCashDKK());
    }

    @Test
    void testBuildPortfolioWithMultipleStocks() {
        transactionRepo.clear();
        transactionRepo.writeTransaction(new Transaction(7, user.getUserId(), new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, 5));
        transactionRepo.writeTransaction(new Transaction(8, user.getUserId(), new Date(), "GOOG", 2800.0, "DKK", OrderType.BUY, 2));

        Portfolio portfolio = PortfolioBuilder.buildPortfolio(user, stockRepo, bondRepo, transactionRepo);

        assertEquals(2, portfolio.getHoldings().size());
        assertTrue(portfolio.getHoldings().containsKey("aapl"));
        assertTrue(portfolio.getHoldings().containsKey("goog"));
    }

    @Test
    void testBuildPortfolioWithInvalidTransaction() {
        transactionRepo.clear();
        transactionRepo.writeTransaction(new Transaction(9, user.getUserId(), new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, -5)); // invalid qty

        Portfolio portfolio = PortfolioBuilder.buildPortfolio(user, stockRepo, bondRepo, transactionRepo);

        assertTrue(portfolio.getHoldings().isEmpty());
        assertEquals(user.getInitialCashDKK(), portfolio.getCashBalance(), 0.01);
    }
}
