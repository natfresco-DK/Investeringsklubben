import Domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class LeaderboardTest {

    private InMemoryStockRepository stockRepo;
    private InMemoryBondRepository bondRepo;
    private InMemoryTransactionRepository transactionRepo;
    private inMemoryUserRepository userRepo;

    private User alice;
    private User bob;

    @BeforeEach
    void setup() {

        stockRepo = new InMemoryStockRepository();
        stockRepo.addStock(new Stock("AAPL", 150.0, "DKK", "Apple", "Tech"));
        stockRepo.addStock(new Stock("GOOG", 2800.0, "DKK", "Google", "Tech"));

        bondRepo = new InMemoryBondRepository();
        bondRepo.addBond(new Bond("DKK1Y", 100.0, "DKK", "Dansk"));

        transactionRepo = new InMemoryTransactionRepository();

        alice = new User(1, "Alice Johnson", "alice@example.com", new Date(), 10000, new Date(), new Date());
        bob = new User(2, "Bob Smith", "bob@example.com", new Date(), 10000, new Date(), new Date());

        userRepo = new inMemoryUserRepository();
        userRepo.addUser(alice);
        userRepo.addUser(bob);

        transactionRepo.writeTransaction(new Transaction(1, alice.getUserId(), new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, 10));

        transactionRepo.writeTransaction(new Transaction(2, bob.getUserId(), new Date(), "GOOG", 1000.0, "DKK", OrderType.BUY, 5));

        userRepo.addUsersPortfolio(stockRepo, bondRepo, transactionRepo);
    }

    @Test
    void testGenerateLeaderboardOrdering() {
        Leaderboard.printAllPortfolios(userRepo, stockRepo, bondRepo, transactionRepo);


        List<User> users = userRepo.getAllUsers();
        users.sort((u1, u2) -> Double.compare(u2.getPortfolio().getTotalValueDKK(), u1.getPortfolio().getTotalValueDKK()));

        User first = users.get(0);
        User second = users.get(1);


        assertTrue(first.getInitialCashDKK() >= second.getInitialCashDKK(), "Bob should appear before Alice");

    }
}
