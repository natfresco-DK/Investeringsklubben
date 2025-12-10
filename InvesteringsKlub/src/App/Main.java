package App;

import CSVHandler.*;
import Domain.Bond;
import UI.ConsoleInterface;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting program...");

        //Load Stock repo
        StockRepository stockRepo = new CSVStockRepository();

        //Load Transaction repo
        TransactionRepository transactionRepo = new CSVTransactionRepository();

        //Load Bond repo
        BondRepository bondRepo = new CSVBondRepository();

        //Load User repo
        UserRepository userRepo = new CSVUserRepository();

        //Regenerate Users Portfolio from transactions
        userRepo.addUsersPortfolio(stockRepo, bondRepo, transactionRepo);

        // Start console interface
        ConsoleInterface console = new ConsoleInterface(userRepo, stockRepo, bondRepo, transactionRepo);
        console.start();

    }
}
