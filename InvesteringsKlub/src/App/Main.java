package App;

import CSVHandler.*;
import UI.ConsoleInterface;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting program...");

        //Load Stock repo
        StockRepository stockRepo = new CSVStockRepository();

        //Load Transaction repo
        TransactionRepository transactionRepo = new CSVTransactionRepository();

        //Load User repo
        UserRepository userRepo = new CSVUserRepository();

        //Regenerate Users Portfolio from transactions
        userRepo.addUsersPortfolio(stockRepo, transactionRepo);

        // Start console interface
        ConsoleInterface console = new ConsoleInterface(userRepo, stockRepo, transactionRepo);
        console.start();

        // Test
        System.out.println("\n--- All bonds from CSV ---");
        BondRepository bondRepo = new CSVBondRepository();
        java.util.List<Domain.Bond> bonds = bondRepo.getAllBonds();
        if (bonds == null || bonds.isEmpty()) {
            System.out.println("No bonds found (check CSV path and format).");
        } else {
            for (Domain.Bond b : bonds) {
                System.out.println(b);
            }
        }
        System.out.println("--- End of bonds ---\n");



    }
}
