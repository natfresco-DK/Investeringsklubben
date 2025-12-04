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

    public void rebuildHoldingsfromTransactions(TransactionRepository transactionRepo, StockRepository stockRepo) {
        List<Transaction> transactions = transactionRepo.getTransactionsByUserId(owner.getUserId());
        for (Transaction trx : transactions) {
            if (trx.getOrderType() == OrderType.BUY) {
                updateHolding(trx.getTicker(), trx.getQuantity(), trx.getPrice(), true, stockRepo);
            } else if (trx.getOrderType() == OrderType.SELL) {
                updateHolding(trx.getTicker(), trx.getQuantity(), trx.getPrice(), false, stockRepo);
            }
        }
    }




    //Samlet investeret beløb i DKK (antal * købspris).
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

            double oldPrice = h.getCurrentPriceDKk();
            h.updateCurrentPriceDKK();
            double newPrice = h.getCurrentPriceDKk();

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
                        .append(h.getCurrentPriceDKk())
                        .append(" DKK\n");
            }
        }

        sb.append("Samlet værdi af holdings: ")
                .append(calculateCurrentHoldingsValueDKK(stockRepo))
                .append(" DKK\n");

        return sb.toString();
    }















}
