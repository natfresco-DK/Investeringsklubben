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








}
