package Domain;

import Builder.PortfolioBuilder;
import CSVHandler.*;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    public static void printAllPortfolios(
            CSVUserRepository userRepo,
            String stocksFile,
            String transactionsFile
    ) {

        // Load repositories
        CSVStockRepository stockRepo = new CSVStockRepository();
        stockRepo.loadFromCSV(stocksFile);

        CSVTransactionRepository transactionRepo = new CSVTransactionRepository();
        transactionRepo.loadFromCSV(transactionsFile);

        // Assign portfolios to users
        userRepo.addUsersPortfolio(stockRepo, transactionRepo);

        System.out.println("\n===== USER PORTFOLIO LEADERBOARD =====\n");

        // Convert to list so we can sort
        var users = new java.util.ArrayList<>(userRepo.getAllUsers());

        // Sort descending by portfolio value
        users.sort((u1, u2) ->
                Double.compare(
                        u2.getPortfolio().getTotalValueDKK(),
                        u1.getPortfolio().getTotalValueDKK()
                )
        );

        for (User u : users) {
            Portfolio p = u.getPortfolio();

            System.out.println("---------------------------------------");
            System.out.println("User: " + u.getFullName());
            System.out.println("Total Portfolio Value: " + p.getTotalValueDKK() + " DKK\n");

            p.printHoldings();
        }

        System.out.println("===== END OF LEADERBOARD =====");
    }
}

