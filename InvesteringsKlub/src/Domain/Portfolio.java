package Domain;

import CSVHandler.*;


import java.util.*;

public class Portfolio {
    protected double cashBalance;
    protected double totalValueDKK;
    protected User owner;
    protected HashMap<String, Holding> holdings = new HashMap<>();

    public Portfolio(){}
    public Portfolio(User user,double cashBalance){
        owner = user;
        this.cashBalance = cashBalance;
        totalValueDKK = cashBalance;
    }

    public double getTotalValueDKK() {
        return totalValueDKK;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public User getOwner() {
        return owner;
    }

    public HashMap<String,Holding> getHoldings() {
        return holdings;
    }

    public void addHolding(Holding holding, StockRepository stockRepo) {
        holdings.put(holding.getTicker(),holding);
        updateTotalValue(stockRepo);
    }

    public void removeHolding(String ticker, StockRepository stockRepo){
        holdings.remove(ticker);
        updateTotalValue(stockRepo);
    }

    public void updateTotalValue(StockRepository stockRepo){
        double value = 0;
        for(Holding h: holdings.values()){
            h.updateCurrentPriceDKK();
            value += h.getCurrentPriceDKk() * h.getQuantity();
        }
        totalValueDKK = value;
    }

    public boolean buyStock(String ticker, int qty, StockRepository stockRepo, TransactionRepository transactionRepo) {
        return executeTrade(ticker, qty, OrderType.BUY, stockRepo, transactionRepo);
    }

    public boolean sellStock(String ticker, int qty, StockRepository stockRepo, TransactionRepository transactionRepo) {
        return executeTrade(ticker, qty, OrderType.SELL, stockRepo, transactionRepo);
    }

    private boolean executeTrade(String ticker, int qty, OrderType orderType,
                                 StockRepository stockRepo, TransactionRepository transactionRepo) {

        Stock stock = stockRepo.getStockByTicker(ticker);
        if (stock == null) {
            System.out.println("Stock not found: " + ticker);
            return false;
        }

        double price = stock.getPrice();
        double totalValue = price * qty;

        Holding holding = getHoldings().get(ticker);

        if (orderType == OrderType.BUY) {
            if (getCashBalance() < totalValue) {
                System.out.println("Not enough funds. Required: " + totalValue +
                        ", Available: " + getCashBalance());
                return false;
            }

            // Deduct balance
            setCashBalance(getCashBalance() - totalValue);

            // Update holdings
            updateHolding(ticker, qty, price, true, stockRepo);

        } else if (orderType == OrderType.SELL) {
            if (holding == null || holding.getQuantity() < qty) {
                System.out.println("Not enough shares to sell. Holding: " +
                        (holding != null ? holding.getQuantity() : 0));
                return false;
            }

            // Add balance
            setCashBalance(getCashBalance() + totalValue);

            // Update holdings
            updateHolding(ticker, qty, price, false, stockRepo);
        }

        // Log transaction
        int trxNumber = transactionRepo.getNextTransactionId();
        Transaction trx = new Transaction(trxNumber, getOwner().getUserId(), new Date(),
                ticker, price, stock.getCurrency(), orderType, qty);
        transactionRepo.writeTransaction(trx);

        return true;
    }

    private void updateHolding(String ticker, int qty, double price, boolean isBuy, StockRepository stockRepo) {
        Holding holding = getHoldings().get(ticker);

        if (isBuy) {
            if (holding == null) {
                holding = new Holding(ticker, qty, stockRepo); // pass repo here
            } else {
                int newQuantity = holding.getQuantity() + qty;
                holding.setQuantity(newQuantity);
            }
            addHolding(holding, stockRepo);
        } else {
            int newQuantity = holding.getQuantity() - qty;
            if (newQuantity > 0) {
                holding.setQuantity(newQuantity);
                addHolding(holding, stockRepo);
            } else {
                removeHolding(ticker, stockRepo);
            }
        }
    }

}
