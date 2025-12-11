package Domain;

import Builder.PortfolioBuilder;
import CSVHandler.*;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    public static void printAllPortfolios(UserRepository userRepo, StockRepository stockRepo, BondRepository bondRepo, TransactionRepository transactionRepo) {
        // Assign portfolios to users
        userRepo.addUsersPortfolio(stockRepo, bondRepo,transactionRepo);
        System.out.println(userRepo.getUserById(1).getPortfolio().getTotalValueDKK());
        System.out.println("\n===== USER PORTFOLIO LEADERBOARD =====\n");

        List<User> users = userRepo.getAllUsers();
        System.out.println(users.get(1).getPortfolio().getTotalValueDKK());

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

