package Domain;

import CSVHandler.*;

public class Holding {
    protected String ticker;
    protected int quantity;
    protected double purchasePriceDKK;
    protected double currentPriceDKK;
    protected StockRepository stockRepo;

    public Holding(){}
    public Holding(String ticker, int quantity, double purchasePriceDKK) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.purchasePriceDKK = purchasePriceDKK;
        this.currentPriceDKK = purchasePriceDKK; // initially same as purchase
    }
    public Holding(String ticker, int quantity, StockRepository stockRepo){
        this.ticker = ticker;
        this.quantity = quantity;
        this.stockRepo = stockRepo;

        Stock stock = stockRepo.getStockByTicker(ticker);
        if(stock != null) {
            this.purchasePriceDKK = stock.getPrice();
            this.currentPriceDKK = stock.getPrice();
        }
    }

    public String getTicker() {
        return ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPurchasePriceDKK() {
        return purchasePriceDKK;
    }

    public double getCurrentPriceDKk() {
        return currentPriceDKK;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setCurrentPriceDKK(double currentPriceDKK) {
        this.currentPriceDKK = currentPriceDKK;
    }

    public void setPurchasePriceDKK(double purchasePriceDKK) {
        this.purchasePriceDKK = purchasePriceDKK;
    }

    public void updateCurrentPriceDKK(){
        Stock stock = stockRepo.getStockByTicker(ticker);
        this.currentPriceDKK = stock.getPrice();
    }
}
