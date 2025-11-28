public abstract class TradableItem {
    protected String ticker;
    protected double price;
    protected String currency;

    public TradableItem(String ticker, double price, String currency){
        this.ticker = ticker;
        this.price = price;
        this.currency = currency;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTicker() {
        return ticker;
    }
}
