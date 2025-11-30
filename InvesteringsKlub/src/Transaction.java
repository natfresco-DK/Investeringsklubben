import java.util.*;

public class Transaction {
    protected int ID;
    protected int userID;
    protected Date date;
    protected String ticker;
    protected double price;
    protected String currency;
    protected OrderType orderType;
    protected int quantity;

    public Transaction(){}

    public Transaction(int ID,int userID, Date date, String ticker,
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

}
