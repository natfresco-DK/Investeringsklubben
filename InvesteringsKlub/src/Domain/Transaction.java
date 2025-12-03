package Domain;

import java.util.*;
import java.text.SimpleDateFormat;

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
        // Use ISO-8601 format for stable CSV output (e.g. 2025-12-02T23:24:40Z)
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        String dateStr = (date != null) ? fmt.format(date) : "";
        return ID + ";"
                + userID + ";"
                + dateStr + ";"
                + ticker + ";"
                + price + ";"
                + currency + ";"
                + orderType + ";"
                + quantity;
    }
}
