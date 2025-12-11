package UI;

import CSVHandler.*;
import Domain.*;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class ConsoleInterface {

    private StockRepository stockRepo;
    private BondRepository bondRepo;
    private TransactionRepository transactionRepo;
    private UserRepository userRepo;
    private User currentUser;
    private Scanner scanner;

    public ConsoleInterface(UserRepository userRepo, StockRepository stockRepo, BondRepository bondRepo, TransactionRepository transactionRepo) {
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
            System.out.println("Choose 0 to Exit");
            System.out.print("Choose login type: ");
            String loginChoice = scanner.nextLine().trim();

            switch (loginChoice) {
                case "1": userLogin(); break;
                case "2": leaderLogin(); break;
                case "0": exit = true; System.out.println("Goodbye!."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ----------------------------
    // User Login & Menu
    // ----------------------------
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
                //case "1": showStockMarket(); break;
                case "1": showMarketSelectionMenu(); break;
                case "2": showPortfolio(); break;
                case "3": showTransactionsForCurrentUser(); break;
                case "4": buyStockOrBond(); break;
                case "5": sellStockOrBond(); break;
                case "0": exit = true; System.out.println("Exiting User Menu."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showUserMenu() {
        System.out.println("\n=== User Menu ===");
        System.out.println("1. View Stock or Bond Market");
        System.out.println("2. View Portfolio");
        System.out.println("3. View Transaction History");
        System.out.println("4. Buy Stock or Bond");
        System.out.println("5. Sell Stock or Bond");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    // ----------------------------
    // Leader Login & Menu
    // ----------------------------
    private void leaderLogin() {
        System.out.println("Hello Leader");
        boolean exit = false;

        while (!exit) {
            showLeaderMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": showStockMarket(); break;
                case "2":
                    Leaderboard.printAllPortfolios(userRepo,stockRepo,bondRepo,transactionRepo);
                    break;
                case "3": viewTransactionHistoryForUser(); break;
                case "4": viewMostBoughtStock(); break;
                case "5": viewMostSoldStock(); break;
                case "6": viewStocksSortedByMostBought(); break;
                case "7": viewMostSoldStocksBySector(); break;
                case "8": leaderSettingsMembers(); break;
                case "0": exit = true; System.out.println("Exiting Leader Menu."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }


    private void showLeaderMenu() {
        System.out.println("\n=== Leader Menu ===");
        System.out.println("1. View Stock Market");
        System.out.println("2. View List of Members Portfolios");
        System.out.println("3. View Transaction History for a User");
        System.out.println("4. View Most Bought Stock");
        System.out.println("5. View Most Sold Stock");
        System.out.println("6. View Stocks Sorted by Most Bought");
        System.out.println("7. View Most Sold Stocks by Sector");
        System.out.println("8. View Member Settings Menu");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

// Member Settings Menu - Placeholder for future implementation

    private void showSettingsMemberMenu(){
        System.out.println("\n=== Settings Menu ===");
        System.out.println("1. Add member");
        System.out.println("2. Remove member" + "Fremtidig opdatierng");
        System.out.println("3. Edit member" + "Fremtidig opdatierng");
        System.out.println("0. Back to Leader Menu");
        System.out.print("Choose an option: ");
    }

    private void leaderSettingsMembers() {
        System.out.println("Hello Leader - Member Settings");
        boolean exit = false;

        while (!exit) {
            showSettingsMemberMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": addMember(); break;
               // case "2": removeMember(); break;
               // case "3": editMember(); break;
                case "0":
                    exit = true;
                    System.out.println("Exiting Leader Menu - Member Settings.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addMember() {
        System.out.println("\n=== Add New Member ===");
        
        try {
            // Generer nyt bruger ID
            int newUserId = userRepo.getAllUsers().stream()
                .mapToInt(User::getUserId)
                .max()
                .orElse(0) + 1;
            
            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine().trim();
            
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            
            System.out.print("Enter birth date (dd-MM-yyyy): ");
            String birthDateStr = scanner.nextLine().trim();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = sdf.parse(birthDateStr);
            
            System.out.print("Enter initial cash (DKK, minimum 10000): ");
            int initialCash = Integer.parseInt(scanner.nextLine().trim());
            
            // Valider minimum startkapital
            if (initialCash < 10000) {
                System.out.println("Error: Initial cash must be at least 10000 DKK!");
                return;
            }
            
            // Opret ny bruger
            User newUser = User.createNewMember(newUserId, fullName, email, birthDate, initialCash);
            
            // Tilføj bruger til repository
            userRepo.addUser(newUser);
            
            System.out.println("\nMember added successfully!");
            System.out.println("User ID: " + newUserId);
            System.out.println("Name: " + fullName);
            System.out.println("Initial Cash: " + initialCash + " DKK");
            
        } catch (Exception e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }






    // ----------------------------
    // Shared Methods
    // ----------------------------

   private void showMarketSelectionMenu(){
        boolean back = false;
        while(!back){
            System.out.println("\n=== Market Selection Menu ===");
            System.out.println("1. View Stock Market");
            System.out.println("2. View Bond Market");
            System.out.println("0. Back to User Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": showStockMarket(); break;
                case "2": showBondMarket(); break;
                case "0": back = true; break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
   }

    private void showStockMarket() {
        System.out.println("\n--- Stock Market ---");

        // Gruppér aktier efter sektor
        Map<String, List<Stock>> stocksBySector = new HashMap<>();
        for (Stock stock : stockRepo.getAllStocks()) {
            String sector = stock.getSector();
            if (sector == null || sector.trim().isEmpty()) {
                sector = "Other";
            }
            stocksBySector.computeIfAbsent(sector, k -> new ArrayList<>()).add(stock);
        }

        // Vis aktier grupperet efter sektor
        for (Map.Entry<String, List<Stock>> entry : stocksBySector.entrySet()) {
            System.out.println("\n=== " + entry.getKey() + " ===");
            for (Stock stock : entry.getValue()) {
                System.out.println("Ticker: " + stock.getTicker() + " | Name: " + stock.getName() + " | Price: " + stock.getPrice() + " " + stock.getCurrency());
            }
        }
    }

    private void showBondMarket(){
        System.out.println("\n--- Bond Market ---");
        BondRepository bondRepo = new CSVBondRepository();
        List<Bond> bonds = bondRepo.getAllBonds();
        for(Bond bond : bonds){
            System.out.println("Ticker: " + bond.getTicker() + " | Name: " + bond.getName() + " | Price: " + bond.getPrice() + " " + bond.getCurrency());
        }
    }

    private void showPortfolio() {
        System.out.println("\n--- Portfolio ---");
        System.out.println("Cash: " + currentUser.getPortfolio().getCashBalance() + " DKK");
        currentUser.getPortfolio().getHoldings().forEach((ticker, holding) -> {
                double percentChange = 0.0;

                if (holding.getPurchasePriceDKK() > 0) {
                    percentChange =
                            ((holding.getCurrentPriceDKK() - holding.getPurchasePriceDKK())
                                    / holding.getPurchasePriceDKK()) * 100.0;
                }
                System.out.println(ticker + " | Qty: " + holding.getQuantity() +
                        " | Current Price DKK: " + holding.getCurrentPriceDKK() +
                        " | Purchase Price DKK: " + holding.getPurchasePriceDKK() +
                        " | Percent " + String.format("%.2f", percentChange) + "%"
                );
            });
                System.out.println("Total Value: " + currentUser.getPortfolio().getTotalValueDKK() + " DKK");
    }

    // ----------------------------
    // User Transaction History
    // ----------------------------
    private void showTransactionsForCurrentUser() {
        List<Transaction> transactions = transactionRepo.getTransactionsByUserId(currentUser.getUserId());

        System.out.println("\n--- Transaction History for " + currentUser.getFullName() + " ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found for this user.");
            return;
        }

        // Print header
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

    private void buyStockOrBond(){
        boolean back = false;
        while(!back){
            System.out.println("\n=== Buy Stock or Bond ===");
            System.out.println("1. Buy stock");
            System.out.println("2. Buy bond");
            System.out.println("0. Back to User Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": buyStock(); break;
                case "2": buyBond(); break;
                case "0": back = true; break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void sellStockOrBond(){
        boolean back = false;
        while(!back){
            System.out.println("\n=== Sell Stock or Bond ===");
            System.out.println("1. Sell stock");
            System.out.println("2. Sell bond");
            System.out.println("0. Back to User Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": sellStock(); break;
                case "2": sellBond(); break;
                case "0": back = true; break;
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void buyStock() {
        // Vis stock market først
        showStockMarket();
        buy();
    }

    private void sellStock() {
        showPortfolio();
        sell();
    }

    private void buyBond(){
        showBondMarket();
        buy();
    }

    private void sellBond(){
        showPortfolio();
        sell();
    }

    private void buy(){
        System.out.print("\nEnter ticker to buy: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().buyStockOrBond(ticker, qty, stockRepo, transactionRepo, bondRepo);
        if(success) System.out.println("bought successfully!");
        else System.out.println("Failed to buy.");
    }

    private void sell(){
        System.out.print("\nEnter ticker to sell: ");
        String ticker = scanner.nextLine().trim();
        System.out.print("Enter quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());
        boolean success = currentUser.getPortfolio().sellStockOrBond(ticker, qty, stockRepo, transactionRepo, bondRepo);
        if(success) System.out.println("Stock sold successfully!");
        else System.out.println("Failed to sell stock.");
    }

    // ----------------------------
    // Leader Methods
    // ----------------------------
    private void viewAllMemberPortfolios() {
        System.out.println("\n--- All Member Portfolios ---");
        for (User user : userRepo.getAllUsers()) {
            System.out.println("User: " + user.getFullName() + " | Cash: " + user.getPortfolio().getCashBalance());
            user.getPortfolio().getHoldings().forEach((ticker, holding) ->
                    System.out.println("  " + ticker + " | Qty: " + holding.getQuantity() + " | Current Price DKK: " + holding.getCurrentPriceDKK())
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

        System.out.println("\n--- Transaction History for " + user.getFullName() + " ---");
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

    private void viewMostBoughtStock() {
        System.out.println("\n--- Most Bought Stock ---");
        List<Transaction> allTransactions = transactionRepo.getAllTransactions();
        Map<String, Integer> buyCounts = allTransactions.stream()
                .filter(trx -> trx.getOrderType() == OrderType.BUY)
                .collect(Collectors.groupingBy(Transaction::getTicker, Collectors.summingInt(Transaction::getQuantity)));
        if (buyCounts.isEmpty()) { System.out.println("No BUY transactions found."); return; }
        String mostBought = Collections.max(buyCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
        int qty = buyCounts.get(mostBought);
        System.out.println("Most Bought Stock: " + mostBought + " | Quantity: " + qty);
    }

    private void viewMostSoldStock() {
        System.out.println("\n--- Most Sold Stock ---");
        List<Transaction> allTransactions = transactionRepo.getAllTransactions();
        Map<String, Integer> sellCounts = allTransactions.stream()
                .filter(trx -> trx.getOrderType() == OrderType.SELL)
                .collect(Collectors.groupingBy(Transaction::getTicker, Collectors.summingInt(Transaction::getQuantity)));
        if (sellCounts.isEmpty()) { System.out.println("No SELL transactions found."); return; }
        String mostSold = Collections.max(sellCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
        int qty = sellCounts.get(mostSold);
        System.out.println("Most Sold Stock: " + mostSold + " | Quantity: " + qty);
    }

    private void viewStocksSortedByMostBought() {
        System.out.println("\n--- Stocks Sorted by Most Bought ---");
        List<Transaction> allTransactions = transactionRepo.getAllTransactions();
        Map<String, Integer> buyCounts = allTransactions.stream()
                .filter(trx -> trx.getOrderType() == OrderType.BUY)
                .collect(Collectors.groupingBy(Transaction::getTicker, Collectors.summingInt(Transaction::getQuantity)));

        if (buyCounts.isEmpty()) {
            System.out.println("No BUY transactions found.");
            return;
        }

        // Gruppér efter sektor
        Map<String, Map<String, Integer>> stocksBySector = new HashMap<>();
        for (Map.Entry<String, Integer> entry : buyCounts.entrySet()) {
            String ticker = entry.getKey();
            Integer quantity = entry.getValue();
            Stock stock = stockRepo.getStockByTicker(ticker);

            if (stock != null) {
                String sector = stock.getSector();
                if (sector == null || sector.trim().isEmpty()) {
                    sector = "Other";
                }
                stocksBySector.computeIfAbsent(sector, k -> new HashMap<>()).put(ticker, quantity);
            }
        }

        // Vis sorteret efter sektor
        for (Map.Entry<String, Map<String, Integer>> sectorEntry : stocksBySector.entrySet()) {
            System.out.println("\n=== " + sectorEntry.getKey() + " ===");
            sectorEntry.getValue().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> System.out.println(entry.getKey() + " | Quantity Bought: " + entry.getValue()));
        }
    }

    private void viewMostSoldStocksBySector() {
        System.out.println("\n--- Most Sold Stocks by Sector ---");
        List<Transaction> allTransactions = transactionRepo.getAllTransactions();
        List<Transaction> sellTransactions = allTransactions.stream()
                .filter(trx -> trx.getOrderType() == OrderType.SELL)
                .collect(Collectors.toList());

        if (sellTransactions.isEmpty()) {
            System.out.println("No SELL transactions found.");
            return;
        }

        // Gruppér aktier efter sektor med deres solgte mængder
        Map<String, Map<String, Integer>> stocksBySector = new HashMap<>();
        for (Transaction trx : sellTransactions) {
            Stock stock = stockRepo.getStockByTicker(trx.getTicker());
            if (stock != null) {
                String sector = stock.getSector();
                if (sector == null || sector.trim().isEmpty()) {
                    sector = "Other";
                }
                Map<String, Integer> stocksInSector = stocksBySector.computeIfAbsent(sector, k -> new HashMap<>());
                stocksInSector.put(trx.getTicker(), stocksInSector.getOrDefault(trx.getTicker(), 0) + trx.getQuantity());
            }
        }

        if (stocksBySector.isEmpty()) {
            System.out.println("No stocks found with sector information.");
            return;
        }

        // Vis top 3 mest solgte aktier per sektor
        for (Map.Entry<String, Map<String, Integer>> sectorEntry : stocksBySector.entrySet()) {
            System.out.println("\n=== " + sectorEntry.getKey() + " ===");
            sectorEntry.getValue().entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(3)
                    .forEach(entry -> System.out.println(entry.getKey() + " | Quantity Sold: " + entry.getValue()));
        }
    }
}
