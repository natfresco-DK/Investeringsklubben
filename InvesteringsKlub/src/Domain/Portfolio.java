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
    public HashMap<String,Holding> getHoldings() {
        return holdings;
    }

    //Setters
    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    //Holdings
    public void addHolding(Holding holding, StockRepository stockRepo) {
        holdings.put(holding.getTicker(),holding);
        updateTotalValue(stockRepo);
    }
    public void removeHolding(String ticker, StockRepository stockRepo){
        holdings.remove(ticker);
        updateTotalValue(stockRepo);
    }

    //Update portfolio total value including cash balance
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

    //Executing trades
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
                // BUY: Weighted average purchase price
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

    //Rebuilding holdings from transactions
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

    //Print Holdings
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

    //Samlet investeret beløb i DKK (antal * købspris)
     public double calculateTotalInvestedDKK() {
         double total = 0.0;
         for (Holding h : holdings.values()) {
             double investedInHolding = h.getQuantity() * h.getPurchasePriceDKK();
             System.out.println("[INVESTERET] " + h.getTicker() + ": "
                     + h.getQuantity() + " stk * " + h.getPurchasePriceDKK()
                     + " = " + investedInHolding + " DKK");

             total += investedInHolding;
         }
         System.out.println("[INVESTERET TOTAL] " + total + " DKK\n");
         return total;
     }

    // Samlet nuværende værdi af alle holdings i DKK (antal * nuværende pris).
    // Opdaterer samtidig currentPriceDKK på hver holding ud fra stockRepo.
    public double calculateCurrentHoldingsValueDKK(StockRepository stockRepo) {
        double value = 0.0;
        for (Holding h : holdings.values()) {

            double oldPrice = h.getCurrentPriceDKK();
            h.updateCurrentPriceDKK();
            double newPrice = h.getCurrentPriceDKK();

            System.out.println("[OPDATERING] " + h.getTicker()
                    + " gammel pris: " + oldPrice
                    + " → ny pris: " + newPrice);

            double holdingValue = newPrice * h.getQuantity();

            System.out.println("[NUVÆRENDE VÆRDI] " + h.getTicker() + ": "
                    + h.getQuantity() + " stk * " + newPrice
                    + " = " + holdingValue + " DKK");

            value += holdingValue;
        }
        System.out.println("[HOLDINGS VÆRDI TOTAL] " + value + " DKK\n");
        return value;
    }

     //Samlet porteføljeværdi inkl. kontantbeholdning.
     public double calculatePortfolioValueIncludingCashDKK(StockRepository stockRepo) {
         double holdingsValue = calculateCurrentHoldingsValueDKK(stockRepo);
         double total = holdingsValue + cashBalance;

         System.out.println("[PORTFØLJE TOTAL] Holdings: " + holdingsValue
                 + " + Kontanter: " + cashBalance
                 + " = " + total + " DKK\n");

         return total;
     }

     //Reelt afkast i DKK (nuværende værdi inkl. kontanter minus investeret beløb).
     public double calculateRealReturnDKK(StockRepository stockRepo) {
         double invested = calculateTotalInvestedDKK();
         double current = calculateCurrentHoldingsValueDKK(stockRepo);

         double realReturn = current - invested;

         System.out.println("[AFKAST] Nuværende: " + current
                 + " - Investering: " + invested
                 + " = " + realReturn + " DKK\n");

         return realReturn;
     }

    // Procentmæssig stigning i porteføljen.
    public double calculateReturnPercentage(StockRepository stockRepo) {
        double invested = calculateTotalInvestedDKK();
        if (invested == 0) {
            System.out.println("[PROCENT] Ingen investering → 0%");
            return 0.0;
        }

        double realReturn = calculateRealReturnDKK(stockRepo);
        double percentage = (realReturn / invested) * 100.0;

        System.out.println("[PROCENT STIGNING] (" + realReturn + " / "
                + invested + ") * 100 = " + percentage + "%\n");

        return percentage;
    }

    public String toString(StockRepository stockRepo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Portefølje for ").append(owner.getFullName()).append("\n");
        sb.append("Kontantbeholdning: ").append(cashBalance).append(" DKK\n");
        sb.append("Holdings:\n");

        if (holdings.isEmpty()) {
            sb.append("Ingen aktier i porteføljen.\n");
        } else {
            for (Holding h : holdings.values()) {
                sb.append("- ")
                        .append(h.getTicker())
                        .append(": ")
                        .append(h.getQuantity())
                        .append(" stk | Købspris: ")
                        .append(h.getPurchasePriceDKK())
                        .append(" DKK | Nuværende pris: ")
                        .append(h.getCurrentPriceDKK())
                        .append(" DKK\n");
            }
        }

        sb.append("Samlet værdi af holdings: ")
                .append(calculateCurrentHoldingsValueDKK(stockRepo))
                .append(" DKK\n");

        return sb.toString();
    }
}
