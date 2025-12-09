package Builder;

import CSVHandler.*;
import Domain.*;

import java.util.*;

public class PortfolioBuilder {

    // Byg portfolio ud fra transaktioner
    public static Portfolio buildPortfolio(User user, StockRepository stockRepo,
                                           TransactionRepository transactionRepo) {
        Portfolio portfolio = new Portfolio(user, user.getInitialCashDKK());

        // Hent transaktioner for brugeren
        List<Transaction> transactions = new ArrayList<>(transactionRepo.getTransactionsByUserId(user.getUserId()));

        // Sorter efter dato
        transactions.sort(Comparator.comparing(Transaction::getDate));

        // Rebuild holdings
        portfolio.rebuildHoldingsFromTransactions(transactions, stockRepo);

        return portfolio;
    }

    // Print portfolio
    public static void printPortfolio(User user, StockRepository stockRepo) {
        Portfolio portfolio = user.getPortfolio();

        System.out.println("\n===== PORTFOLIO FOR " + user.getFullName() + " =====");
        System.out.println("Cash Balance: " + portfolio.getCashBalance() + " DKK");

        System.out.println("\nStock Holdings:");
        for (Holding h : portfolio.getHoldings().values()) {
            System.out.println("- " + h.getTicker() +
                    ": " + h.getQuantity() + " qty | " +
                    "Purchase price: " + h.getPurchasePriceDKK() + " DKK | " +
                    "Current price: " + h.getCurrentPriceDKK() + " DKK"
            );
        }

        System.out.println("\nBond Holdings:");
        for (Holding h : portfolio.getBondHoldings().values()) {
            System.out.println("- " + h.getTicker() +
                    ": " + h.getQuantity() + " qty | " +
                    "Purchase price: " + h.getPurchasePriceDKK() + " DKK | " +
                    "Current price: " + h.getCurrentPriceDKK() + " DKK"
            );
        }

        double totalInvested = calculateTotalInvestedDKK(portfolio);
        double totalCurrent = calculateCurrentHoldingsValueDKK(portfolio, stockRepo);
        double realReturn = totalCurrent - totalInvested;
        double percentReturn = totalInvested > 0 ? (realReturn / totalInvested) * 100.0 : 0.0;

        System.out.println("\nTotal Invested: " + totalInvested + " DKK");
        System.out.println("Current Value of Holdings: " + totalCurrent + " DKK");
        System.out.println("Real Return: " + realReturn + " DKK");
        System.out.printf("Return Percentage: %.2f%%\n", percentReturn);
    }

    // ----------------------------
    // Helper methods
    // ----------------------------
    private static double calculateTotalInvestedDKK(Portfolio portfolio) {
        double total = 0.0;
        for (Holding h : portfolio.getHoldings().values()) {
            total += h.getPurchasePriceDKK() * h.getQuantity();
        }
        for (Holding h : portfolio.getBondHoldings().values()) {
            total += h.getPurchasePriceDKK() * h.getQuantity();
        }
        return total;
    }

    private static double calculateCurrentHoldingsValueDKK(Portfolio portfolio, StockRepository stockRepo) {
        double total = 0.0;
        for (Holding h : portfolio.getHoldings().values()) {
            Stock s = stockRepo.getStockByTicker(h.getTicker());
            if (s != null) h.setCurrentPriceDKK(s.getPrice());
            total += h.getCurrentPriceDKK() * h.getQuantity();
        }
        for (Holding h : portfolio.getBondHoldings().values()) {
            total += h.getCurrentPriceDKK() * h.getQuantity();
        }
        return total;
    }
}
