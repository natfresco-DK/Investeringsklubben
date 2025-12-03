package Domain;
//jjjndd
import Domain.User;
import CSVHandler.CSVStockRepository;
import CSVHandler.CSVTransactionRepository;
import java.util.Scanner;

public class ConsoleInterface {
    private CSVStockRepository stockRepo;
    private CSVTransactionRepository transactionRepo;
    private User currentUser;
    private Scanner scanner;

    public ConsoleInterface(User user, CSVStockRepository stockRepo, CSVTransactionRepository transactionRepo) {
        this.currentUser = user;
        this.stockRepo = stockRepo;
        this.transactionRepo = transactionRepo;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
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
                    buyStock();
                    break;
                case "5":
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
                System.out.println(ticker + " | Qty: " + holding.getQuantity() + " | Current Price DKK: " + holding.getCurrentPriceDKk())
        );
        System.out.println("Total Value: " + currentUser.getPortfolio().getTotalValueDKK() + " DKK");
    }

    private void showTransactions() {
        System.out.println("\n--- Transaction History ---");
        // Her skal du kunne læse transactions fra transactionRepo, filtreret på currentUser.getUserId()
        System.out.println("Feature: Print transactions for user ID " + currentUser.getUserId());
    }

    private void buyStock() {
        System.out.print("Enter ticker to buy: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().buyStock(ticker, qty, stockRepo, transactionRepo);
        if(success) System.out.println("Stock bought successfully!");
    }

    private void sellStock() {
        System.out.print("Enter ticker to sell: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().sellStock(ticker, qty, stockRepo, transactionRepo);
        if(success) System.out.println("Stock sold successfully!");
    }
}