package UI;

import CSVHandler.UserRepository;
import CSVHandler.TransactionRepository;
import CSVHandler.StockRepository;
import Domain.User;
import java.util.Scanner;

public class ConsoleInterface {
    private StockRepository stockRepo;
    private TransactionRepository transactionRepo;
    private UserRepository userRepo;
    private User currentUser;
    private Scanner scanner;

    public ConsoleInterface(UserRepository userRepo, StockRepository stockRepo, TransactionRepository transactionRepo) {
        this.userRepo = userRepo;
        this.stockRepo = stockRepo;
        this.transactionRepo = transactionRepo;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        System.out.println("Input user ID");
        int userID = Integer.parseInt(scanner.nextLine());
        setCurrentUser(userID);
        System.out.println("Hello " + currentUser.getFullName());
        while (!exit) {
            showMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showStockMarket();
                    break;
                case "2":
                    showPortfolio();
                    break;
                case "3":
                    showTransactions();
                    break;
                case "4":
                    showStockMarket();
                    buyStock();
                    break;
                case "5":
                    showPortfolio();
                    sellStock();
                    break;
                case "0":
                    exit = true;
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View Stock Market");
        System.out.println("2. View Portfolio");
        System.out.println("3. View Transaction History");
        System.out.println("4. Buy Stock");
        System.out.println("5. Sell Stock");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void showStockMarket() {
        System.out.println("\n--- Stock Market ---");
        stockRepo.getAllStocks().forEach(stock ->
                System.out.println(stock.getTicker() + " | " + stock.getName() + " | Price: " + stock.getPrice() + " " + stock.getCurrency())
        );
    }

    private void showPortfolio() {
        System.out.println("\n--- Portfolio ---");
        System.out.println("Cash: " + currentUser.getPortfolio().getCashBalance() + " DKK");
        currentUser.getPortfolio().getHoldings().forEach((ticker, holding) ->
                System.out.println(ticker + " | Qty: " + holding.getQuantity() + " | Current Price DKK: " + holding.getCurrentPriceDKK())
        );
        System.out.println("Total Value: " + currentUser.getPortfolio().getTotalValueDKK() + " DKK" + "\n");
    }

    private void showTransactions() {
        System.out.println("\n--- Transaction History ---");
        System.out.println("Print transactions for user " + currentUser.getFullName());
        currentUser.printTransactionHistory(transactionRepo,currentUser.getUserId());
    }

    private void buyStock() {
        System.out.print("Enter ticker to buy: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().buyStock(ticker, qty, stockRepo, transactionRepo);
        if(success){
            System.out.println("Stock bought successfully!");
        } else {
            System.out.println("Stock not bought");
        }
    }

    private void sellStock() {
        System.out.print("Enter ticker to sell: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().sellStock(ticker, qty, stockRepo, transactionRepo);
        if(success) System.out.println("Stock sold successfully!");
    }

    public void setCurrentUser(int userID) {
        User currentUser = userRepo.getUserById(userID);
        this.currentUser = currentUser;
    }
}