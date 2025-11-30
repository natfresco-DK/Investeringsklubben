import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    protected int userId;
    protected String fullName;
    protected String email;
    protected Date birthDate;
    protected int initialCashDKK;
    protected double balance;
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
        balance = initialCashDKK;
        createdAt = created;
        this.lastUpdated = lastUpdated;
    }
    public User(int id, String name, String email, Date birth, int initialCashDKK,double balance, Date created, Date lastUpdated){
        userId = id;
        fullName = name;
        this.email = email;
        birthDate = birth;
        this.initialCashDKK = initialCashDKK;
        this.balance = balance;
        createdAt = created;
        this.lastUpdated = lastUpdated;
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

    public double getBalance() {
        return balance;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean buyStock(String ticker, int qty){
        Stock stock = CSVStockRepository.getStockByTicker(ticker);
        if(stock==null){
            System.out.println("Stock not found" + ticker);
            return false;
        }

        double price = stock.getPrice();
        double totalCost = price * qty;
        //validate balance
        if(getBalance()<totalCost){
            System.out.println("Not enough founds. Required: " + totalCost + ", available: " + getBalance());
            return false;
        }
        //deduct balance
        setBalance(balance-=totalCost);

        //update portfolio
        Holding holding =portfolio.getHoldings().getOrDefault(ticker,new Holding(ticker,0));
        double totalCostExisting = holding.getQuantity() * holding.getPurchasePriceDKK() + totalCost;
        int newQuantity = holding.getQuantity() + qty;
        double newPurchasePrice = totalCostExisting / newQuantity;

        holding.setQuantity(newQuantity);
        holding.setPurchasePriceDKK(newPurchasePrice);
        holding.setPurchasePriceDKK(price);
        portfolio.addHolding(holding);

        //log Transaction
        return true;
    }

    public void sellStock(String ticker, int qty){

    }
}
