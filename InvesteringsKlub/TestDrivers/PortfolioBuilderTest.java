import Builder.*;
import CSVHandler.*;
import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioBuilderTest {

    private InMemoryStockRepository stockRepo;
    private InMemoryTransactionRepository transactionRepo;
    private User user;
    private File tempCsvFile;

    @BeforeEach
    void setup() throws IOException {
        // Set up in-memory stocks
        stockRepo = new InMemoryStockRepository();
        stockRepo.addStock(new Stock("AAPL", 150.0, "DKK", "Apple Inc.", "Tech"));
        stockRepo.addStock(new Stock("GOOG", 2800.0, "DKK", "Google Inc.", "Tech"));

        // In-memory transactions
        transactionRepo = new InMemoryTransactionRepository();

        // Test user
        user = new User(1, "Alice Johnson", "alice@example.com",
                new Date(1990, 1, 1), 10000,
                new Date(), new Date());

        // Temporary CSV file with transactions
        tempCsvFile = File.createTempFile("transactions", ".csv");
        tempCsvFile.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempCsvFile))) {
            writer.write("userId,date,ticker,price,currency,orderType,quantity\n");
            writer.write("1,2025-11-30,AAPL,150.0,DKK,BUY,10\n");
            writer.write("1,2025-11-30,AAPL,150.0,DKK,SELL,5\n");
        }
    }
    @Test
    void testBuildPortfolioFromCSVAllFields() {
        Portfolio portfolio = PortfolioBuilder.buildPortfolio(
                user, tempCsvFile.getAbsolutePath(), stockRepo, transactionRepo
        );

        //Check cash balance
        double expectedCash = 10000.0 - (10 * 150.0) + (5 * 150.0); // bought 10, sold 5
        assertEquals(expectedCash, portfolio.getCashBalance(), 0.01);

        //Check holdings
        Holding aapl = portfolio.getHoldings().get("AAPL");
        assertNotNull(aapl);
        assertEquals(5, aapl.getQuantity()); // 10 bought - 5 sold
        assertEquals(150.0, aapl.getPurchasePriceDKK(), 0.01);
        assertEquals(150.0, aapl.getCurrentPriceDKk(), 0.01);

        //Check total value
        portfolio.updateTotalValue(stockRepo);
        double expectedTotalValue = 5 * 150.0;
        assertEquals(expectedTotalValue, portfolio.getTotalValueDKK(), 0.01);

        //Check transactions logged
        List<Transaction> transactions = transactionRepo.getTransactions();
        assertEquals(2, transactions.size());

        //Check first transaction (BUY)
        Transaction trx1 = transactions.get(0);
        assertEquals(user.getUserId(), trx1.getUserID());
        assertEquals("AAPL", trx1.getTicker());
        assertEquals(10, trx1.getQuantity());
        assertEquals(150.0, trx1.getPrice(), 0.01);
        assertEquals("DKK", trx1.getCurrency());
        assertEquals(OrderType.BUY, trx1.getOrderType());

        //Check second transaction (SELL)
        Transaction trx2 = transactions.get(1);
        assertEquals(user.getUserId(), trx2.getUserID());
        assertEquals("AAPL", trx2.getTicker());
        assertEquals(5, trx2.getQuantity());
        assertEquals(150.0, trx2.getPrice(), 0.01);
        assertEquals("DKK", trx2.getCurrency());
        assertEquals(OrderType.SELL, trx2.getOrderType());

        //Check transactions logged
        assertEquals(2, transactionRepo.getTransactions().size());
    }

    @Test
    void testBuildPortfolioFromCSVHoldings() {
        Portfolio portfolio = PortfolioBuilder.buildPortfolio(
                user, tempCsvFile.getAbsolutePath(), stockRepo, transactionRepo
        );

        //Check cash balance
        double expectedCash = 10000.0 - (10 * 150.0) + (5 * 150.0); // bought 10, sold 5
        assertEquals(expectedCash, portfolio.getCashBalance(), 0.01);

        //Check holdings
        Holding aapl = portfolio.getHoldings().get("AAPL");
        assertNotNull(aapl);
        assertEquals(5, aapl.getQuantity()); // 10 bought - 5 sold
        assertEquals(150.0, aapl.getPurchasePriceDKK(), 0.01);

        //Check total value
        portfolio.updateTotalValue(stockRepo);
        double expectedTotalValue = 5 * 150.0;
        assertEquals(expectedTotalValue, portfolio.getTotalValueDKK(), 0.01);

        //Check transactions logged
        assertEquals(2, transactionRepo.getTransactions().size());
    }

    @Test
    void testBuildPortfolioFrom(){
        //check holdings

        //update stock value

        //check holdings again

    }
}