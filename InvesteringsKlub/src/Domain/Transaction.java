package Domain;

import java.util.*;
import CSVHandler.TransactionRepository;

public class Transaction{
    protected int ID;
    protected int userID;
    protected Date date;
    protected String ticker;
    protected double price;
    protected String currency;
    protected OrderType orderType;
    protected int quantity;

    public Transaction(){}

    public Transaction(int ID, int userID, Date date, String ticker,
                       double price, String currency, OrderType orderType, int quantity){
        this.ID = ID;
        this.userID = userID;
        this.date = date;
        this.ticker = ticker;
        this.price = price;
        this.currency = currency;
        this.orderType = orderType;
        this.quantity = quantity;

    }

    public int getID() {
        return ID;
    }
    public int getUserID() {
        return userID;
    }
    public Date getDate() {
        return date;
    }
    public String getTicker() {
        return ticker;
    }
    public double getPrice() {
        return price;
    }
    public String getCurrency() {
        return currency;
    }
    public OrderType getOrderType() {
        return orderType;
    }
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return ID + ";"
                + userID + ";"
                + date + ";"
                + ticker + ";"
                + price + ";"
                + currency + ";"
                + orderType + ";"
                + quantity;
    }
    public static void readTransactionHistory(TransactionRepository transactionRepo){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Type userID: ");
        int userId = Integer.parseInt(scanner.nextLine());

        List<Transaction> userTransactions = transactionRepo.readTransactionsByUserId(userId);

        if (userTransactions.isEmpty()) {
            System.out.println("No transactions found for this user.");
        } else {
            userTransactions.forEach(System.out::println); //Automatically uses the toString method in Transaction class
        }
    }

}
