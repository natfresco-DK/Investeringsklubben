package Domain;

import CSVHandler.*;

public class Holding {
    protected String ticker;
    protected int quantity;
    protected double purchasePriceDKK;
    protected double currentPriceDKK;
    protected StockRepository stockRepo;
    protected BondRepository bondRepo;

    public Holding() {}

    public Holding(String ticker, int quantity, double purchasePriceDKK) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.purchasePriceDKK = purchasePriceDKK;
        this.currentPriceDKK = purchasePriceDKK; // initially same as purchase
    }

    // Getters
    public String getTicker() { return ticker; }
    public int getQuantity() { return quantity; }
    public double getPurchasePriceDKK() { return purchasePriceDKK; }
    public double getCurrentPriceDKK() { return currentPriceDKK; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTicker(String ticker) { this.ticker = ticker; }
    public void setCurrentPriceDKK(double currentPriceDKK) { this.currentPriceDKK = currentPriceDKK; }
    public void setPurchasePriceDKK(double purchasePriceDKK) { this.purchasePriceDKK = purchasePriceDKK; }

    public void setStockRepo(StockRepository stockRepo) { this.stockRepo = stockRepo; }
    public void setBondRepo(BondRepository bondRepo) { this.bondRepo = bondRepo; }

    // Update current price
    public void updateCurrentPriceDKK() {
        if (stockRepo != null && stockRepo.getStockByTicker(ticker) != null) {
            // Hvis tickeren findes som stock
            this.currentPriceDKK = stockRepo.getStockByTicker(ticker).getPrice();
        } else if (bondRepo != null && bondRepo.getBondByTicker(ticker) != null) {
            // Hvis tickeren findes som bond
            this.currentPriceDKK = bondRepo.getBondByTicker(ticker).getPrice();
        } else {
            // Hvis tickeren ikke findes i nogen repo, behold purchasePriceDKK
            this.currentPriceDKK = this.purchasePriceDKK;
        }
    }
}
