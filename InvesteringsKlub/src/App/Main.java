package App;
//jjnmrdkd
import CSVHandler.CSVTransactionRepository;
import Domain.CSVUserRepository;
import Domain.OrderType;
import Domain.Transaction;
import Domain.User;

import java.util.*;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static CSVTransactionRepository transactionRepo = new CSVTransactionRepository();
    static CSVUserRepository userRepo = new CSVUserRepository("InvesteringsKlub/CSVRepository/users.csv");


    public static void main(String[] args) {

        while (true) {
            System.out.println("\n=== Investeringsklub Menu ===");
            System.out.println("1. Lav køb/salg");
            System.out.println("2. Vis alle transaktioner");
            System.out.println("3. Klubleder menu");
            System.out.println("0. Afslut");
            System.out.print("Vælg en mulighed: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createTransaction();
                case 2 -> showAllTransactions();
                case 3 -> leaderMenu();
                case 0 -> {
                    System.out.println("Farvel!");
                    return;
                }
                default -> System.out.println("Ugyldigt valg. Prøv igen.");
            }
        }
    }

    // ----------------------------------------------------
    // USER STORY 11
    // ----------------------------------------------------

    private static void createTransaction() {
        try {
            int id = transactionRepo.getNextTransactionId();

            System.out.print("Indtast bruger-ID: ");
            int userId = Integer.parseInt(scanner.nextLine());

            Date date = new Date();

            System.out.print("Indtast ticker: ");
            String ticker = scanner.nextLine();

            System.out.print("Indtast pris: ");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.print("Indtast valuta: ");
            String currency = scanner.nextLine();

            System.out.print("Køb eller salg (buy/sell): ");
            String typeInput = scanner.nextLine().trim().toUpperCase();
            OrderType type = OrderType.valueOf(typeInput);

            System.out.print("Indtast antal: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            Transaction trx = new Transaction(id, userId, date, ticker, price, currency, type, quantity);
            transactionRepo.writeTransaction(trx);

            System.out.println("Transaktion oprettet!");
        } catch (Exception e) {
            System.out.println("Fejl i input. Prøv igen.");
        }
    }

    private static void showAllTransactions() {
        List<Transaction> transactions = transactionRepo.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("Ingen transaktioner fundet.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }

    // ----------------------------------------------------
    // USER STORY 7 MENU
    // ----------------------------------------------------

    private static void leaderMenu() {

        while (true) {
            System.out.println("\n=== Klubleder Menu ===");
            System.out.println("1. Sortér efter højeste værdi");
            System.out.println("2. Vis portefølje pr. bruger");
            System.out.println("3. Vis portefølje pr. sektor");
            System.out.println("0. Tilbage");
            System.out.print("Vælg: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> sortByHighestValue();
                case 2 -> portfolioByUser();
                case 3 -> portfolioBySector();
                case 0 -> { return; }
                default -> System.out.println("Ugyldigt valg.");
            }
        }
    }

    // ----------------------------------------------------
    // USER STORY 7 – funktioner
    // ----------------------------------------------------

    private static void sortByHighestValue() {

        List<Transaction> trxs = transactionRepo.getAllTransactions();

        trxs.sort((a, b) -> Double.compare(
                b.getPrice() * b.getQuantity(),
                a.getPrice() * a.getQuantity()
        ));

        System.out.println("\n--- Sorteret efter højeste værdi ---");
        for (Transaction t : trxs) {
            double value = t.getPrice() * t.getQuantity();
            System.out.println(t.getTicker() + " | Værdi: " + value + " " + t.getCurrency());
        }
    }

    private static void portfolioByUser() {

        List<Transaction> trxs = transactionRepo.getAllTransactions();
        Map<Integer, Double> valueMap = new HashMap<>();

        for (Transaction t : trxs) {
            double value = t.getPrice() * t.getQuantity();
            valueMap.put(t.getUserID(), valueMap.getOrDefault(t.getUserID(), 0.0) + value);
        }

        System.out.println("\n--- Portefølje pr. bruger ---");
        for (int userId : valueMap.keySet()) {
            User u = userRepo.getUserById(userId);
            String name = (u != null) ? u.getFullName() : "Ukendt Bruger";

            System.out.println(name + ": " + valueMap.get(userId) + " DKK");
        }
    }

    private static void portfolioBySector() {

        Map<String, String> sectorMap = new HashMap<>();
        sectorMap.put("NOVO-B", "Healthcare");
        sectorMap.put("NZYM-B", "Healthcare");
        sectorMap.put("AMBU-B", "Healthcare");
        sectorMap.put("DANSKE", "Finance");
        sectorMap.put("CARL-B", "Consumer Goods");
        sectorMap.put("ROCK-B", "Industrial");
        sectorMap.put("PANDORA", "Consumer Goods");
        sectorMap.put("TRYG", "Insurance");
        sectorMap.put("JYSK", "Retail");
        sectorMap.put("DSV", "Transport");
        sectorMap.put("NKT", "Industrial");
        sectorMap.put("ISS", "Services");

        List<Transaction> trxs = transactionRepo.getAllTransactions();
        Map<String, Double> sectorValue = new HashMap<>();

        for (Transaction t : trxs) {
            double value = t.getPrice() * t.getQuantity();
            String sector = sectorMap.getOrDefault(t.getTicker(), "Unknown");
            sectorValue.put(sector, sectorValue.getOrDefault(sector, 0.0) + value);
        }

        System.out.println("\n--- Portefølje pr. sektor ---");
        for (String sector : sectorValue.keySet()) {
            System.out.println(sector + ": " + sectorValue.get(sector) + " DKK");
        }
    }
}
