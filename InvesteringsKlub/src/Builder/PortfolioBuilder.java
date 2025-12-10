package Builder;
import CSVHandler.*;
import Domain.*;

import java.util.*;

public class PortfolioBuilder {

    public static Portfolio buildPortfolio(User user, StockRepository stockRepo, BondRepository bondRepo,
                                           TransactionRepository transactionRepo) {
        Portfolio portfolio = new Portfolio(user, user.getInitialCashDKK());

        List<Transaction> transactions = new ArrayList<>(
                    transactionRepo.getTransactionsByUserId(user.getUserId()));

        transactions.sort(Comparator.comparing(Transaction::getDate));

        portfolio.rebuildHoldingsfromTransactions(transactions, stockRepo);

        // Update portfolio totals
        portfolio.updateTotalValue(stockRepo, bondRepo);

        return portfolio;
    }

    public static void printPortfolio(User user, StockRepository stockRepo) {
        Portfolio portfolio = user.getPortfolio();

        System.out.println("\n===== PORTFOLIO FOR " + user.getFullName() + " =====");

        System.out.println("CashBalance: " + portfolio.getCashBalance() + " DKK");
        System.out.println("\nStock holdings:");

        for (Holding h : portfolio.getHoldings().values()) {
            System.out.println(
                    "- " + h.getTicker() +
                            ": " + h.getQuantity() + " qty. | " +
                            "Purchase price: " + h.getPurchasePriceDKK() + " DKK | " +
                            "Current price: " + h.getCurrentPriceDKK() + " DKK"
            );
        }

        System.out.println("\nTotal invested: " + portfolio.calculateTotalInvestedDKK());
        System.out.println("Total present value: " + portfolio.calculateCurrentHoldingsValueDKK(stockRepo));
        System.out.println("Real return: " + portfolio.calculateRealReturnDKK(stockRepo));
        System.out.println("Return in percent: " + portfolio.calculateReturnPercentage(stockRepo) + "%");
    }







}
