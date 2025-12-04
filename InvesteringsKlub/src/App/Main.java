package App;

import Domain.Leaderboard;
import Domain.User;

import java.util.List;

public class Main {
    public static void main (String[] args){
        System.out.println("Starter programmet...");
        List<User> users = Leaderboard.loadAndAssignPortfolios(
                "InvesteringsKlub/CSVRepository/users.csv",
                "InvesteringsKlub/CSVRepository/stockMarket.csv",
                "InvesteringsKlub/CSVRepository/transactions.csv"
        );

        Leaderboard.printAllPortfolios(users);
    }
    }

