package Builder;
import CSVHandler.*;
import Domain.*;
import java.io.*;
import java.util.*;

public class PortfolioBuilder {

    public static Portfolio buildPortfolio(User user, String transactionsFile, StockRepository stockRepo,
                                           TransactionRepository transactionRepo) {
        Portfolio portfolio = new Portfolio(user, user.getInitialCashDKK());

        List<Transaction> transactions = new ArrayList<>(
                    transactionRepo.getTransactionsByUserId(user.getUserId()));

        transactions.sort(Comparator.comparing(Transaction::getDate));

        portfolio.rebuildHoldingsfromTransactions(transactions, stockRepo);

        // Update portfolio totals
        portfolio.updateTotalValue(stockRepo);

        return portfolio;
    }

    public static void printPortfolio(User user, StockRepository stockRepo) {
        Portfolio portfolio = user.getPortfolio();

        System.out.println("\n===== PORTFØLJE FOR " + user.getFullName() + " =====");

        System.out.println("Kontantbeholdning: " + portfolio.getCashBalance() + " DKK");
        System.out.println("\nAktiebeholdninger:");

        for (Holding h : portfolio.getHoldings().values()) {
            System.out.println(
                    "- " + h.getTicker() +
                            ": " + h.getQuantity() + " stk. | " +
                            "Købspris: " + h.getPurchasePriceDKK() + " DKK | " +
                            "Nuværende pris: " + h.getCurrentPriceDKk() + " DKK"
            );
        }

        System.out.println("\nSamlet investeret: " + portfolio.calculateTotalInvestedDKK());
        System.out.println("Samlet nuværende værdi: " + portfolio.calculateCurrentHoldingsValueDKK(stockRepo));
        System.out.println("Reelt afkast: " + portfolio.calculateRealReturnDKK(stockRepo));
        System.out.println("Afkast i procent: " + portfolio.calculateReturnPercentage(stockRepo) + "%");
    }







}
