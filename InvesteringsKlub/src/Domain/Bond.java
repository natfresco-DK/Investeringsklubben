package Domain;

public class Bond extends TradableItem {

    protected String name;
    protected double couponRate;      // Kuponrente
    protected String issueDate;       // Udstedelsesdato
    protected String maturityDate;    // Forfaldsdato

    public Bond() {}

    public Bond(String ticker, double price, String currency, String name,
                double couponRate, String issueDate, String maturityDate) {
        super(ticker, price, currency);
        this.name = name;
        this.couponRate = couponRate;
        this.issueDate = issueDate;
        this.maturityDate = maturityDate;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getCouponRate() {
        return couponRate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    @Override
    public String toString() {
        return "Ticker: " + ticker +
                ", Price: " + price +
                ", Currency: " + currency +
                ", Name: " + name +
                ", Coupon Rate: " + couponRate +
                ", Issue Date: " + issueDate +
                ", Maturity Date: " + maturityDate;
    }
}
