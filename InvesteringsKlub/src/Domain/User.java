package Domain;

import java.util.*;
import CSVHandler.TransactionRepository;
import java.text.SimpleDateFormat;

public class User {
    protected int userId;
    protected String fullName;
    protected String email;
    protected Date birthDate;
    protected int initialCashDKK;
    protected Date createdAt;
    protected Date lastUpdated;
    protected Portfolio portfolio;

    public User(){}
    public User(int id, String name, String email, Date birth, int initialCashDKK, Date created, Date lastUpdated){
        userId = id;
        fullName = name;
        this.email = email;
        birthDate = birth;
        this.initialCashDKK = initialCashDKK;
        createdAt = created;
        this.lastUpdated = lastUpdated;
        this.portfolio = new Portfolio(this, initialCashDKK);
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public int getInitialCashDKK() {
        return initialCashDKK;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public void sellStock(String ticker, int qty){

    }
    public boolean readTransactionHistory(TransactionRepository transactionRepo) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type userID: ");
        int userId = Integer.parseInt(scanner.nextLine());

        List<Transaction> userTransactions = transactionRepo.readTransactionsByUserId(userId);

        if (userTransactions.isEmpty()) {
            System.out.println("No transactions found for this user.");
            return false;
        } else {
            // Headers
            System.out.printf("%-5s %-8s %-12s %-8s %-10s %-10s %-10s %-8s%n",
                    "ID", "UserID", "Date", "Ticker", "Price", "Currency", "Type", "Qty");

            System.out.println("--------------------------------------------------------------------------------");

            // Format for the output
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (Transaction t : userTransactions) {
                System.out.printf("%-5d %-8d %-12s %-8s %-10.2f %-10s %-10s %-8d%n",
                        t.getID(),
                        t.getUserID(),
                        sdf.format(t.getDate()),
                        t.getTicker(),
                        t.getPrice(),
                        t.getCurrency(),
                        t.getOrderType(),
                        t.getQuantity());
                return true;
            }
        }
        return true;
        }
    }
