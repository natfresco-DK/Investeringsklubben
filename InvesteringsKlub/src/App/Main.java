package App;

import CSVHandler.*;
import UI.ConsoleInterface;
import Domain.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting program...");

        // Load Stock repository
        StockRepository stockRepo = new CSVStockRepository();

        // Load Bond repository
        CSVBondRepository bondRepo = new CSVBondRepository();

        // Load Transaction repository
        TransactionRepository transactionRepo = new CSVTransactionRepository();

        // Load User repository
        UserRepository userRepo = new CSVUserRepository();

        // Regenerate Users Portfolio from transactions
        userRepo.addUsersPortfolio(stockRepo, transactionRepo);

        // Start console interface
        ConsoleInterface console = new ConsoleInterface(userRepo, stockRepo, bondRepo, transactionRepo);
        console.start();

        // Test: Display all bonds from CSV
        System.out.println("\n--- All bonds from CSV ---");
        java.util.List<Bond> bonds = bondRepo.getAllBonds();
        if (bonds == null || bonds.isEmpty()) {
            System.out.println("No bonds found (check CSV path and format).");
        } else {
            for (Bond b : bonds) {
                System.out.println(b);
            }
        }
        System.out.println("--- End of bonds ---\n");
    }
}
