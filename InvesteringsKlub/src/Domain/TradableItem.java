package Domain;

public abstract class TradableItem {
    protected String ticker;
    protected double price;
    protected String currency;

    public TradableItem(){}
    public TradableItem(String ticker, double price, String currency){
        this.ticker = ticker;
        this.price = price;
        this.currency = currency;
    }

    //Getters
    public double getPrice() {
        return price;
    }
    public String getCurrency() {
        return currency;
    }
    public String getTicker() {
        return ticker;
    }

    //Setters
    public void setPrice(double price) {
        this.price = price;
    }

}
