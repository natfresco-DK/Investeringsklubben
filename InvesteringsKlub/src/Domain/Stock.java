package Domain;

public class Stock extends TradableItem {
    protected String name;
    protected String sector;

    public Stock(){}
    public Stock (String ticker, double price, String currency, String name, String sector){
        super(ticker,price,currency);
        this.name = name;
        this.sector = sector;
    }

    public String getName() {
        return name;
    }

    public String getSector() {
        return sector;
    }

}
