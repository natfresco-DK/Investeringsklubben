package App;


import CSVHandler.*;
import UI.ConsoleInterface;


public class Main {
    public static void main(String[] args) {

        


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

