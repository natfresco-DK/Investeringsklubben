package App;

import Domain.*;
import CSVHandler.*;
import UI.ConsoleInterface;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starter programmet...");
        
        // Load users and assign portfolios
//        List<User> users = Leaderboard.loadAndAssignPortfolios(
//                "InvesteringsKlub/CSVRepository/users.csv",
//                "InvesteringsKlub/CSVRepository/stockMarket.csv",
//                "InvesteringsKlub/CSVRepository/transactions.csv"
//        );
        // Load repositories
        CSVStockRepository stockRepo = new CSVStockRepository();
        stockRepo.loadFromCSV("InvesteringsKlub/CSVRepository/stockMarket.csv");
        
        CSVTransactionRepository transactionRepo = new CSVTransactionRepository();
        transactionRepo.loadFromCSV("InvesteringsKlub/CSVRepository/transactions.csv");

        CSVUserRepository userRepo = new CSVUserRepository("InvesteringsKlub/CSVRepository/users.csv");
        userRepo.addUsersPortfolio(stockRepo,transactionRepo);
        
        // Start console interface
        ConsoleInterface console = new ConsoleInterface(userRepo, stockRepo, transactionRepo);
        console.start();
    }
}

