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
        double holdingsValue = 0.0;

        for (Holding h : holdings.values()) {
            if (stockRepo != null) {
                Stock s = stockRepo.getStockByTicker(h.getTicker());
                if (s != null) {
                    h.setCurrentPriceDKK(s.getPrice());
                } else {
                    h.updateCurrentPriceDKK();
                }
            } else {
                h.updateCurrentPriceDKK();
            }
            holdingsValue += h.getCurrentPriceDKK() * h.getQuantity();
        }

        this.totalValueDKK = holdingsValue + getCashBalance();
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
                // New holding with initial purchase price
                holding = new Holding(ticker, qty, price);
            } else {
                // Weighted average purchase price for BUY
                int oldQty = holding.getQuantity();
                double oldAvgPrice = holding.getPurchasePriceDKK();
                int newQty = oldQty + qty;
                double newAvgPrice = ((oldQty * oldAvgPrice) + (qty * price)) / newQty;

                holding.setQuantity(newQty);
                holding.setPurchasePriceDKK(newAvgPrice);
            }
            addHolding(holding, stockRepo);
        } else {
            // SELL: Reduce quantity, keep purchase price unchanged
            int newQuantity = holding.getQuantity() - qty;
            if (newQuantity > 0) {
                holding.setQuantity(newQuantity);
                addHolding(holding, stockRepo);
            } else {
                removeHolding(ticker, stockRepo);
            }
        }
    }


    public void rebuildHoldingsfromTransactions(List<Transaction> transactions, StockRepository stockRepo) {
        holdings.clear();
        // Use owner's initial cash
        cashBalance = owner.getInitialCashDKK();
        totalValueDKK = cashBalance;

        if (stockRepo == null) {
            System.out.println("Stock repository is null. Cannot update current prices.");
            return;
        }
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        // Ensure transactions are in chronological order
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getID));

        for (Transaction trx : transactions) {
            String ticker = trx.getTicker();
            int qty = trx.getQuantity();
            if (qty <= 0) {
                System.out.println("Skipping invalid transaction with non-positive quantity: " + qty);
                continue; // Skip invalid transactions
            }
            double price = trx.getPrice();
            if (price <= 0) {
                System.out.println("Skipping invalid transaction with non-positive price: " + price);
                continue;
            }
            OrderType type = trx.getOrderType();

            Holding h = holdings.get(ticker);

            if (type == OrderType.BUY) {
                // Deduct cash
                this.cashBalance -= qty * price;

                if (h == null) {
                    h = new Holding(ticker, qty, price);
                } else {
                    // weighted average purchase price
                    int oldQty = h.getQuantity();
                    double oldAvg = h.getPurchasePriceDKK();
                    int newQty = oldQty + qty;
                    double newAvg = (oldQty * oldAvg + qty * price) / newQty;
                    h.setQuantity(newQty);
                    h.setPurchasePriceDKK(newAvg);
                }
                holdings.put(ticker, h);

            } else if (type == OrderType.SELL) {
                if (h == null) {
                    continue; // Selling non-existing holding: ignore
                }

                int availableQty = h.getQuantity();
                if (qty > availableQty) {
                    continue; // Ignore invalid sell (cannot sell more than owned)
                }
                this.cashBalance += qty * price;

                if (h == null) {
                    // selling non-existing holding: ignore (or handle differently if desired)
                    continue;
                }
                int remaining = h.getQuantity() - qty;
                if (remaining > 0) {
                    h.setQuantity(remaining);
                    holdings.put(ticker, h);
                } else {
                    holdings.remove(ticker);
                }
            }
        }

        // Update current prices for holdings using stockRepo (and compute total)
        double holdingsValue = 0.0;
        for (Holding h : holdings.values()) {
            Stock stock = stockRepo.getStockByTicker(h.getTicker());
            if (stock != null) {
                h.setCurrentPriceDKK(stock.getPrice());
            } else {
                h.setCurrentPriceDKK(h.getPurchasePriceDKK()); // fallback
            }
            holdingsValue += h.getCurrentPriceDKK() * h.getQuantity();
        }
        this.totalValueDKK = this.cashBalance + holdingsValue;
    }

    public void printHoldings() {
        System.out.println("Holdings for user: " + owner.getFullName());

        if (holdings == null || holdings.isEmpty()) {
            System.out.println("  (No holdings)");
            return;
        }

        System.out.println("  Ticker     Quantity     CurrentPriceDKK     TotalDKK");
        System.out.println("  ----------------------------------------------------");

        for (Holding h : holdings.values()) {
            double currentPrice = h.getCurrentPriceDKK();
            double total = currentPrice * h.getQuantity();

            System.out.printf(
                    "  %-10s %-10d %-17.2f %-10.2f%n",
                    h.getTicker(),
                    h.getQuantity(),
                    currentPrice,
                    total
            );
        }

        System.out.println();
    }
}
