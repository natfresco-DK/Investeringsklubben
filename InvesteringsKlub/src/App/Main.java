package App;

import CSVHandler.*;
import UI.ConsoleInterface;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starter programmet...");

        //Load Stock repo
        StockRepository stockRepo = new CSVStockRepository();

        //Load Transaction repo
        CSVTransactionRepository transactionRepo = new CSVTransactionRepository();

        //Load User repo
        CSVUserRepository userRepo = new CSVUserRepository();

        //Regenerate Users Portfolio from transactions
        userRepo.addUsersPortfolio(stockRepo,transactionRepo);
        
        // Start console interface
        ConsoleInterface console = new ConsoleInterface(userRepo, stockRepo, transactionRepo);
        console.start();
    }
}

