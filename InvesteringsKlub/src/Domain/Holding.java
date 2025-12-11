package Domain;

import CSVHandler.*;

public class Holding {
    protected String ticker;
    protected int quantity;
    protected double purchasePriceDKK;
    protected double currentPriceDKK;

    public Holding(){}
    public Holding(String ticker, int quantity, double purchasePriceDKK) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.purchasePriceDKK = purchasePriceDKK;
        this.currentPriceDKK = purchasePriceDKK; // initially same as purchase

    }

    //Getters
    public String getTicker() {
        return ticker;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getPurchasePriceDKK() {
        return purchasePriceDKK;
    }
    public double getCurrentPriceDKK() {
        return currentPriceDKK;
    }

    //Setters
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
}
