package Domain;

import CSVHandler.CSVStockRepository;
import CSVHandler.CSVTransactionRepository;

import java.util.Date;

public class User {
    protected int userId;
    protected String fullName;
    protected String email;
    protected Date birthDate;
    protected int initialCashDKK;
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
        createdAt = created;
        this.lastUpdated = lastUpdated;
        this.portfolio = new Portfolio(this, initialCashDKK);
    }
//jj
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

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    public void sellStock(String ticker, int qty){

    }
    

}
