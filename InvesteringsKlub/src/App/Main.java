package App;

import Domain.*;
import CSVHandler.*;
import UI.ConsoleInterface;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starter programmet...");
        
        // Load users and assign portfolios
        List<User> users = Leaderboard.loadAndAssignPortfolios(
                "InvesteringsKlub/CSVRepository/users.csv",
                "InvesteringsKlub/CSVRepository/stockMarket.csv",
                "InvesteringsKlub/CSVRepository/transactions.csv"
        );

        // Get first user (or let user select)
        User currentUser = users.get(0);
        
        // Load repositories
        CSVStockRepository stockRepo = new CSVStockRepository();
        stockRepo.loadFromCSV("InvesteringsKlub/CSVRepository/stockMarket.csv");
        
        CSVTransactionRepository transactionRepo = new CSVTransactionRepository();
        transactionRepo.loadFromCSV("InvesteringsKlub/CSVRepository/transactions.csv");
        
        // Start console interface
        ConsoleInterface console = new ConsoleInterface(currentUser, stockRepo, transactionRepo);
        console.start();
    }
}

