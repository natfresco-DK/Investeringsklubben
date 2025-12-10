
package Builder;

import CSVHandler.*;
import Domain.*;

import java.util.*;

public class PortfolioBuilder {

    // Build portfolio from transactions
    public static Portfolio buildPortfolio(User user, StockRepository stockRepo,
                                           TransactionRepository transactionRepo) {
        Portfolio portfolio = new Portfolio(user, user.getInitialCashDKK());

        // Fetch transactions for the user
        List<Transaction> transactions = new ArrayList<>(transactionRepo.getTransactionsByUserId(user.getUserId()));

        // Rebuild holdings
        portfolio.rebuildHoldingsFromTransactions(transactions, stockRepo);

        return portfolio;
    }

    // Print portfolio
    public static void printPortfolio(User user, StockRepository stockRepo) {
        Portfolio portfolio = user.getPortfolio();

        System.out.println("\n===== PORTFOLIO FOR " + user.getFullName() + " =====");


        portfolio.printHoldings();


        double totalInvested = portfolio.calculateTotalInvestedDKK();
        double totalCurrent = portfolio.calculateCurrentHoldingsValueDKK(stockRepo, null);
        double realReturn = totalCurrent - totalInvested;
        double percentReturn = totalInvested > 0 ? (realReturn / totalInvested) * 100.0 : 0.0;

        System.out.println("\nTotal Invested: " + totalInvested + " DKK");
        System.out.println("Current Value of Holdings: " + totalCurrent + " DKK");
        System.out.println("Real Return: " + realReturn + " DKK");
        System.out.printf("Return Percentage: %.2f%%\n", percentReturn);
    }
}
