package Domain;

public class Bond extends TradableItem {

    protected String name;


    public Bond() {
    }

    public Bond(String ticker, double price, String currency, String name) {
        super(ticker, price, currency);
        this.name = name;
    }

    //getters
    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "Ticker: " + ticker + ", Price: " + price + ", Currency: " + currency + ", Name: " + name;

    }
}



