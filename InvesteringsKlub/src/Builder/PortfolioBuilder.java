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
}
