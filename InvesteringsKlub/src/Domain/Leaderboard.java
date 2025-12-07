package Domain;

import Builder.PortfolioBuilder;
import CSVHandler.*;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    public static List<User> loadAndAssignPortfolios(
            String usersFile,
            String stocksFile,
            String transactionsFile
    ) {
        // Load users
        CSVUserRepository userRepo = new CSVUserRepository();
        List<User> users = new ArrayList<>(userRepo.getAllUsers());

        // Load stocks
        StockRepository stockRepo = new CSVStockRepository();

        // Load transactions
        TransactionRepository transactionRepo = new CSVTransactionRepository();

        // Assign portfolios
        assignPortfolios(users, stockRepo, transactionRepo, transactionsFile);

        return users;
    }

    public static void assignPortfolios(
            List<User> users,
            StockRepository stockRepo,
            TransactionRepository transactionRepo,
            String transactionsFile
    ) {
        for (User u : users) {
            Portfolio p = PortfolioBuilder.buildPortfolio(u, stockRepo, transactionRepo);
            u.setPortfolio(p);
        }
    }

    public static void printAllPortfolios(List<User> users) {
        System.out.println("\n===== USER PORTFOLIO LEADERBOARD =====\n");

        // Sortér users efter total portfolio value descending
        users.sort((u1, u2) -> Double.compare(u2.getPortfolio().getTotalValueDKK(), u1.getPortfolio().getTotalValueDKK()));

        for (User u : users) {
            Portfolio p = u.getPortfolio();
            if (p == null) {
                System.out.println("User: " + u.getFullName() + " has no portfolio.");
                continue;
            }

            System.out.println("---------------------------------------");
            System.out.println("User: " + u.getFullName());
            System.out.println("Total Portfolio Value: " + p.getTotalValueDKK() + " DKK\n");

            p.printHoldings();
        }

        System.out.println("===== END OF LEADERBOARD =====");
    }
}

