public class Holding {
    protected String ticker;
    protected int quantity;
    protected double purchasePriceDKK;
    protected double currentPriceDKK;

    public Holding(){}
    public Holding(String ticker, int quantity, double purchasePriceDKK, double currentPriceDKK){
        this.ticker = ticker;
        this.quantity = quantity;
        this.purchasePriceDKK = purchasePriceDKK;
        this.currentPriceDKK = currentPriceDKK;
    }
    public Holding(String ticker, int quantity){
        this.ticker = ticker;
        this.quantity = quantity;
        Stock stock = CSVStockRepository.getStockByTicker(ticker);
        this.purchasePriceDKK = stock.getPrice();
        this.currentPriceDKK = stock.getPrice();
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
        Stock stock = CSVStockRepository.getStockByTicker(ticker);
        this.currentPriceDKK = stock.getPrice();
    }
}
