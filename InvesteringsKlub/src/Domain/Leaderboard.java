package Domain;

import CSVHandler.*;

import java.util.List;
import java.util.Map;

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


        public static String generateLeaderboard(CSVHandler.UserRepository userRepo) {
            List<User> users = userRepo.getAllUsers();

            users.sort((u1, u2) ->
                    Double.compare(
                            u2.getPortfolio().getTotalValueDKK(),
                            u1.getPortfolio().getTotalValueDKK()
                    )
            );

            StringBuilder sb = new StringBuilder();
            sb.append("===== USER PORTFOLIO LEADERBOARD =====\n\n");

            for (User u : users) {
                Portfolio p = u.getPortfolio();
                sb.append("---------------------------------------\n");
                sb.append("User: ").append(u.getFullName()).append("\n");
                sb.append("Total Portfolio Value: ")
                        .append(p.getTotalValueDKK()).append(" DKK\n\n");

                Map<String, Holding> holdings = p.getHoldings();
                if (holdings == null || holdings.isEmpty()) {
                    sb.append("(no holdings)\n\n");
                } else {
                    for (Map.Entry<String, Holding> e : holdings.entrySet()) {
                        Holding h = e.getValue();
                        sb.append(String.format("Ticker: %s, Qty: %d, Price: %.2f\n",
                                e.getKey(), h.getQuantity(), h.getPurchasePriceDKK()));
                    }
                    sb.append("\n");
                }
            }

            sb.append("===== END OF LEADERBOARD =====");
            return sb.toString();
        }
    }


