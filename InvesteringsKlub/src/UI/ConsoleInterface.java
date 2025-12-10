package UI;

import CSVHandler.*;
import Domain.*;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class ConsoleInterface {

    private StockRepository stockRepo;
    private CSVBondRepository bondRepo;
    private TransactionRepository transactionRepo;
    private UserRepository userRepo;
    private User currentUser;
    private Scanner scanner;

    public ConsoleInterface(UserRepository userRepo, StockRepository stockRepo,
                            CSVBondRepository bondRepo, TransactionRepository transactionRepo) {
        this.userRepo = userRepo;
        this.stockRepo = stockRepo;
        this.bondRepo = bondRepo;
        this.transactionRepo = transactionRepo;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;

        while (!exit) {
            System.out.println("=== Login Screen ===");
            System.out.println("1. User Login");
            System.out.println("2. Leader Login");
            System.out.println("0. Exit");
            System.out.print("Choose login type: ");
            String loginChoice = scanner.nextLine().trim();

            switch (loginChoice) {
                case "1": userLogin(); break;
                case "2": leaderLogin(); break;
                case "0": exit = true; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }


    // User Login & Menu

    private void userLogin() {
        System.out.print("Input user ID: ");
        int userID = Integer.parseInt(scanner.nextLine());
        currentUser = userRepo.getUserById(userID);

        if (currentUser == null) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("Hello " + currentUser.getFullName());
        boolean exit = false;

        while (!exit) {
            showUserMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": showStockMarket(); break;
                case "2": showBondMarket(); break;
                case "3": showPortfolio(); break;
                case "4": showTransactionsForCurrentUser(); break;
                case "5": buyStock(); break;
                case "6": sellStock(); break;
                case "7": buyBond(); break;
                case "8": sellBond(); break;
                case "0": exit = true; System.out.println("Exiting User Menu."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showUserMenu() {
        System.out.println("\n=== User Menu ===");
        System.out.println("1. View Stock Market");
        System.out.println("2. View Bond Market");
        System.out.println("3. View Portfolio");
        System.out.println("4. View Transaction History");
        System.out.println("5. Buy Stock");
        System.out.println("6. Sell Stock");
        System.out.println("7. Buy Bond");
        System.out.println("8. Sell Bond");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }


    // Show Markets

    private void showStockMarket() {
        System.out.println("\n--- Stock Market ---");
        stockRepo.getAllStocks().forEach(stock ->
                System.out.println(stock.getTicker() + " | " + stock.getName() + " | Price: " + stock.getPrice() + " " + stock.getCurrency())
        );
    }

    private void showBondMarket() {
        System.out.println("\n--- Bond Market ---");
        bondRepo.getAllBonds().forEach(bond ->
                System.out.println(bond.getTicker() + " | " + bond.getName() + " | Price: " + bond.getPrice() + " " + bond.getCurrency())
        );
    }


    // Portfolio

    private void showPortfolio() {
        Portfolio portfolio = currentUser.getPortfolio();
        System.out.println("\n--- Portfolio ---");
        System.out.println("Cash: " + portfolio.getCashBalance() + " DKK");

        System.out.println("\nStocks:");
        portfolio.getHoldings().forEach((ticker, holding) -> {
            double percentChange = 0.0;
            if (holding.getPurchasePriceDKK() > 0) {
                percentChange = ((holding.getCurrentPriceDKK() - holding.getPurchasePriceDKK()) / holding.getPurchasePriceDKK()) * 100.0;
            }
            System.out.println(ticker + " | Qty: " + holding.getQuantity() +
                    " | Current Price DKK: " + holding.getCurrentPriceDKK() +
                    " | Purchase Price DKK: " + holding.getPurchasePriceDKK() +
                    " | Percent " + String.format("%.2f", percentChange) + "%"
            );
        });

        System.out.println("\nBonds:");
        portfolio.getBondHoldings().forEach((ticker, holding) -> {
            System.out.println(ticker + " | Qty: " + holding.getQuantity() +
                    " | Current Price DKK: " + holding.getCurrentPriceDKK() +
                    " | Purchase Price DKK: " + holding.getPurchasePriceDKK()
            );
        });

        System.out.println("Total Portfolio Value: " + portfolio.getTotalValueDKK() + " DKK");
    }


    // User Transaction History

    private void showTransactionsForCurrentUser() {
        List<Transaction> transactions = transactionRepo.getTransactionsByUserId(currentUser.getUserId());

        System.out.println("\n--- Transaction History for " + currentUser.getFullName() + " ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this user.");
            return;
        }

        System.out.printf("%-5s %-10s %-12s %-8s %-10s %-8s %-6s %-8s\n",
                "ID", "UserID", "Date", "Ticker", "Price", "Currency", "Type", "Qty");

        System.out.println("---------------------------------------------------------------");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (Transaction trx : transactions) {
            System.out.printf("%-5d %-10d %-12s %-8s %-10.2f %-8s %-6s %-8d\n",
                    trx.getID(),
                    trx.getUserID(),
                    sdf.format(trx.getDate()),
                    trx.getTicker(),
                    trx.getPrice(),
                    trx.getCurrency(),
                    trx.getOrderType(),
                    trx.getQuantity());
        }
    }


    // Buy/Sell Stocks

    private void buyStock() {
        System.out.print("Enter ticker to buy: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().buyStock(ticker, qty, stockRepo, transactionRepo);
        System.out.println(success ? "Stock bought successfully!" : "Failed to buy stock.");
    }

    private void sellStock() {
        System.out.print("Enter ticker to sell: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().sellStock(ticker, qty, stockRepo, transactionRepo);
        System.out.println(success ? "Stock sold successfully!" : "Failed to sell stock.");
    }


    // Buy/Sell Bonds

    private void buyBond() {
        System.out.print("Enter bond ticker to buy: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().buyBond(ticker, qty, bondRepo, transactionRepo);
        System.out.println(success ? "Bond bought successfully!" : "Failed to buy bond.");
    }

    private void sellBond() {
        System.out.print("Enter bond ticker to sell: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().sellBond(ticker, qty, bondRepo, transactionRepo);
        System.out.println(success ? "Bond sold successfully!" : "Failed to sell bond.");
    }


    // Leader Menu

    private void leaderLogin() {
        System.out.println("Hello Leader");
        boolean exit = false;

        while (!exit) {
            showLeaderMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": showStockMarket(); break;
                case "2": showBondMarket(); break;
                case "3": viewAllMemberPortfolios(); break;
                case "4": viewTransactionHistoryForUser(); break;
                case "0": exit = true; System.out.println("Exiting Leader Menu."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showLeaderMenu() {
        System.out.println("\n=== Leader Menu ===");
        System.out.println("1. View Stock Market");
        System.out.println("2. View Bond Market");
        System.out.println("3. View All Portfolios");
        System.out.println("4. View Transaction History for a User");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void viewAllMemberPortfolios() {
        System.out.println("\n--- All Member Portfolios ---");
        for (User user : userRepo.getAllUsers()) {
            System.out.println("User: " + user.getFullName() + " | Cash: " + user.getPortfolio().getCashBalance());
            user.getPortfolio().getHoldings().forEach((ticker, holding) ->
                    System.out.println("  " + ticker + " | Qty: " + holding.getQuantity() + " | Current Price DKK: " + holding.getCurrentPriceDKK())
            );
            user.getPortfolio().getBondHoldings().forEach((ticker, holding) ->
                    System.out.println("  [Bond] " + ticker + " | Qty: " + holding.getQuantity() + " | Current Price DKK: " + holding.getCurrentPriceDKK())
            );
            System.out.println("  Total Value: " + user.getPortfolio().getTotalValueDKK());
            System.out.println("---------------------------");
        }
    }

    private void viewTransactionHistoryForUser() {
        System.out.print("Type user ID: ");
        int userId = Integer.parseInt(scanner.nextLine().trim());
        User user = userRepo.getUserById(userId);
        if (user == null) { System.out.println("User not found."); return; }

        List<Transaction> transactions = transactionRepo.getTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this user.");
            return;
        }

        System.out.printf("%-5s %-10s %-12s %-8s %-10s %-8s %-6s %-8s\n",
                "ID", "UserID", "Date", "Ticker", "Price", "Currency", "Type", "Qty");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (Transaction trx : transactions) {
            System.out.printf("%-5d %-10d %-12s %-8s %-10.2f %-8s %-6s %-8d\n",
                    trx.getID(), trx.getUserID(), sdf.format(trx.getDate()),
                    trx.getTicker(), trx.getPrice(), trx.getCurrency(),
                    trx.getOrderType(), trx.getQuantity());
        }
    }
}
