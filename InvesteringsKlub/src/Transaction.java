import java.util.*;

public class Transaction {
    protected int ID;
    protected int userID;
    protected Date date;
    protected String ticker;
    protected double price;
    protected String currency;
    protected Enum orderType;
    protected int quantity;

    public Transaction(){}

    public Transaction(int ID,int userID, Date date, String ticker,
                       double price, String currency, Enum orderType, int quantity){

    }

}
