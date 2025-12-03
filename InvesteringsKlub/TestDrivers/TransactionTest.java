import Domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private InMemoryStockRepository stockRepo;
    private InMemoryTransactionRepository transactionRepo;
    private User user;
    private Portfolio portfolio;

    @BeforeEach
    void setup() {
        // In-memory stocks
        stockRepo = new InMemoryStockRepository();
        stockRepo.addStock(new Stock("AAPL", 150.0, "DKK", "Apple inc.","Tech"));
        stockRepo.addStock(new Stock("GOOG", 2800.0, "DKK", "Google inc.","Tech"));

        // In-memory transaction repository
        transactionRepo = new InMemoryTransactionRepository();

        // Test user
        user = new User(
                1,
                "Alice Johnson",
                "alice@example.com",
                new Date(90, 0, 1), // Year = 1990, Month = Jan (0-based)
                10000,
                new Date(),
                new Date()
        );

        portfolio = new Portfolio(user, 10000.0);
    }

    @AfterEach
    void resetSystemIn() {
        System.setIn(System.in);
    }

    @Test
    void testBuyStockLogsTransaction() {
        boolean result = portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);
        assertTrue(result);

        List<Transaction> transactions = transactionRepo.getTransactions();
        assertEquals(1, transactions.size());

        Transaction trx = transactions.get(0);
        assertEquals(user.getUserId(), trx.getUserID());
        assertEquals("AAPL", trx.getTicker());
        assertEquals(10, trx.getQuantity());
        assertEquals(OrderType.BUY, trx.getOrderType());
    }

    @Test
    void testSellStockLogsTransaction() {
        portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);

        boolean result = portfolio.sellStock("AAPL", 5, stockRepo, transactionRepo);
        assertTrue(result);

        List<Transaction> transactions = transactionRepo.getTransactions();
        assertEquals(2, transactions.size());

        Transaction sellTrx = transactions.get(1);
        assertEquals(user.getUserId(), sellTrx.getUserID());
        assertEquals("AAPL", sellTrx.getTicker());
        assertEquals(5, sellTrx.getQuantity());
        assertEquals(OrderType.SELL, sellTrx.getOrderType());
    }

    @Test
    void testSeeUsersTransactionHistoryTrue() {
        portfolio.buyStock("AAPL", 10, stockRepo, transactionRepo);

        // Kald testbar metode direkte med userId
        boolean result = user.readTransactionHistory(transactionRepo, user.getUserId());

        assertTrue(result);
    }

    @Test
    void testSeeUsersTransactionHistoryFalse() {
        // Simuler input med et userID uden transaktioner
        String simulatedInput = "999\n"; // No transactions for this ID
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        boolean result = user.readTransactionHistory(transactionRepo);

        assertFalse(result);
    }
}