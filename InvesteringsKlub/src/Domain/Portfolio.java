package Domain;

import CSVHandler.*;
import java.util.*;

public class Portfolio {
    protected double cashBalance;
    protected double totalValueDKK;
    protected User owner;

    // Separate maps for stocks and bonds
    protected Map<String, Holding> holdings = new HashMap<>();
    protected Map<String, Holding> bondHoldings = new HashMap<>();

    public Portfolio() {}

    public Portfolio(User user, double cashBalance) {
        this.owner = user;
        this.cashBalance = cashBalance;
        this.totalValueDKK = cashBalance;
    }

    // Getters
    public double getTotalValueDKK() {
        return totalValueDKK;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public User getOwner() {
        return owner;
    }

    public Map<String, Holding> getHoldings() {
        return holdings;
    }

    public Map<String, Holding> getBondHoldings() {
        return bondHoldings;
    }

    // Setters
    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    // ----------------------------
    // Utility
    // ----------------------------
    private static String norm(String key) {
        return key == null ? null : key.toLowerCase(Locale.ROOT);
    }

    // ----------------------------
    // Stock Methods
    // ----------------------------
    public void addHolding(Holding holding, StockRepository stockRepo) {
        String key = norm(holding.getTicker());
        holdings.put(key, holding);
        updateTotalValueIncludingBonds(stockRepo, null);
    }

    public Holding getStockHolding(String ticker) {
        return holdings.get(norm(ticker));
    }

    public boolean buyStock(String ticker, int qty, StockRepository stockRepo, TransactionRepository transactionRepo) {
        return executeStockTrade(ticker, qty, OrderType.BUY, stockRepo, transactionRepo);
    }

    public boolean sellStock(String ticker, int qty, StockRepository stockRepo, TransactionRepository transactionRepo) {
        return executeStockTrade(ticker, qty, OrderType.SELL, stockRepo, transactionRepo);
    }

    private boolean executeStockTrade(String ticker, int qty, OrderType orderType,
                                      StockRepository stockRepo, TransactionRepository transactionRepo) {
        Stock stock = stockRepo.getStockByTicker(norm(ticker));
        if (stock == null) {
            System.out.println("Stock not found: " + ticker);
            return false;
        }

        double price = stock.getPrice();
        double totalValue = price * qty;
        Holding holding = getStockHolding(ticker);

        if (orderType == OrderType.BUY) {
            if (getCashBalance() < totalValue) {
                System.out.println("Not enough funds.");
                return false;
            }
            setCashBalance(getCashBalance() - totalValue);
            updateStockHolding(ticker, qty, price, true, stockRepo);
        } else {
            if (holding == null || holding.getQuantity() < qty) {
                System.out.println("Not enough shares to sell.");
                return false;
            }
            setCashBalance(getCashBalance() + totalValue);
            updateStockHolding(ticker, qty, price, false, stockRepo);
        }

        int trxNumber = transactionRepo.getNextTransactionId();
        Transaction trx = new Transaction(trxNumber, getOwner().getUserId(), new Date(),
                ticker, price, stock.getCurrency(), orderType, qty);
        transactionRepo.writeTransaction(trx);

        return true;
    }

    private void updateStockHolding(String ticker, int qty, double price, boolean isBuy, StockRepository stockRepo) {
        String key = norm(ticker);
        Holding holding = holdings.get(key);

        if (isBuy) {
            if (holding == null) {
                holding = new Holding(ticker, qty, price);
            } else {
                int oldQty = holding.getQuantity();
                double oldAvgPrice = holding.getPurchasePriceDKK();
                int newQty = oldQty + qty;
                double newAvgPrice = ((oldQty * oldAvgPrice) + (qty * price)) / newQty;
                holding.setQuantity(newQty);
                holding.setPurchasePriceDKK(newAvgPrice);
            }
            holdings.put(key, holding);
        } else {
            int newQty = holding.getQuantity() - qty;
            if (newQty > 0) {
                holding.setQuantity(newQty);
                holdings.put(key, holding);
            } else {
                holdings.remove(key);
            }
        }

        updateTotalValueIncludingBonds(stockRepo, null);
    }

    // ----------------------------
    // Bond Methods
    // ----------------------------
    public void addBondHolding(Holding holding, BondRepository bondRepo) {
        String key = norm(holding.getTicker());
        bondHoldings.put(key, holding);
        updateTotalValueIncludingBonds(null, bondRepo);
    }

    public Holding getBondHolding(String ticker) {
        return bondHoldings.get(norm(ticker));
    }

    public boolean buyBond(String ticker, int qty, CSVBondRepository bondRepo, TransactionRepository transactionRepo) {
        Bond bond = bondRepo.getBondByTicker(ticker);
        if (bond == null) {
            System.out.println("Bond not found: " + ticker);
            return false;
        }

        double price = bond.getPrice();
        double totalValue = price * qty;

        if (getCashBalance() < totalValue) {
            System.out.println("Not enough cash to buy bond.");
            return false;
        }

        setCashBalance(getCashBalance() - totalValue);

        Holding holding = getBondHolding(ticker);
        if (holding == null) {
            holding = new Holding(ticker, qty, price);
        } else {
            int oldQty = holding.getQuantity();
            double oldAvgPrice = holding.getPurchasePriceDKK();
            int newQty = oldQty + qty;
            double newAvgPrice = ((oldQty * oldAvgPrice) + (qty * price)) / newQty;
            holding.setQuantity(newQty);
            holding.setPurchasePriceDKK(newAvgPrice);
        }
        addBondHolding(holding, bondRepo);

        int trxNumber = transactionRepo.getNextTransactionId();
        Transaction trx = new Transaction(trxNumber, getOwner().getUserId(), new Date(),
                ticker, price, bond.getCurrency(), OrderType.BUY, qty);
        transactionRepo.writeTransaction(trx);

        return true;
    }

    public boolean sellBond(String ticker, int qty, CSVBondRepository bondRepo, TransactionRepository transactionRepo) {
        Holding holding = getBondHolding(ticker);
        if (holding == null || holding.getQuantity() < qty) {
            System.out.println("Not enough bonds to sell.");
            return false;
        }

        Bond bond = bondRepo.getBondByTicker(ticker);
        if (bond == null) {
            System.out.println("Bond not found: " + ticker);
            return false;
        }

        double price = bond.getPrice();
        setCashBalance(getCashBalance() + price * qty);

        int newQty = holding.getQuantity() - qty;
        if (newQty > 0) {
            holding.setQuantity(newQty);
            bondHoldings.put(norm(ticker), holding);
        } else {
            bondHoldings.remove(norm(ticker));
        }

        int trxNumber = transactionRepo.getNextTransactionId();
        Transaction trx = new Transaction(trxNumber, getOwner().getUserId(), new Date(),
                ticker, price, bond.getCurrency(), OrderType.SELL, qty);
        transactionRepo.writeTransaction(trx);

        updateTotalValueIncludingBonds(null, bondRepo);

        return true;
    }

    // ----------------------------
    // Total Value Update
    // ----------------------------
    public void updateTotalValueIncludingBonds(StockRepository stockRepo, BondRepository bondRepo) {
        double holdingsValue = 0.0;

        // Stocks
        for (Holding h : holdings.values()) {
            if (stockRepo != null) {
                Stock s = stockRepo.getStockByTicker(h.getTicker());
                if (s != null) h.setCurrentPriceDKK(s.getPrice());
                else h.updateCurrentPriceDKK();
            } else {
                h.updateCurrentPriceDKK();
            }
            holdingsValue += h.getCurrentPriceDKK() * h.getQuantity();
        }

        // Bonds
        for (Holding h : bondHoldings.values()) {
            if (bondRepo != null) {
                Bond b = bondRepo.getBondByTicker(h.getTicker());
                if (b != null) h.setCurrentPriceDKK(b.getPrice());
                else h.updateCurrentPriceDKK();
            } else {
                h.updateCurrentPriceDKK();
            }
            holdingsValue += h.getCurrentPriceDKK() * h.getQuantity();
        }

        this.totalValueDKK = holdingsValue + getCashBalance();
    }

    // ----------------------------
    // NEW: Print Holdings
    // ----------------------------
    public void printHoldings() {
        System.out.println("Cash: " + getCashBalance() + " DKK");

        System.out.println("\nStocks:");
        for (Holding h : holdings.values()) {
            System.out.println(h.getTicker() + " | Qty: " + h.getQuantity() +
                    " | Purchase Price DKK: " + h.getPurchasePriceDKK() +
                    " | Current Price DKK: " + h.getCurrentPriceDKK());
        }

        System.out.println("\nBonds:");
        for (Holding h : bondHoldings.values()) {
            System.out.println(h.getTicker() + " | Qty: " + h.getQuantity() +
                    " | Purchase Price DKK: " + h.getPurchasePriceDKK() +
                    " | Current Price DKK: " + h.getCurrentPriceDKK());
        }

        System.out.println("\nTotal Portfolio Value: " + getTotalValueDKK() + " DKK");
    }

    public void rebuildHoldingsFromTransactions(List<Transaction> transactions, StockRepository stockRepo) {
    }
}
