package Builder;
import CSVHandler.*;
import Domain.*;
import java.io.*;
import java.util.*;

public class PortfolioBuilder {

    public static Portfolio buildPortfolio(User user, String transactionsFile,
                                           StockRepository stockRepo,
                                           TransactionRepository transactionRepo) {
        Portfolio portfolio = new Portfolio(user, user.getInitialCashDKK());

        //Load Transactions for this user
        List<Transaction> transactions = transactionRepo.getTransactionsByUserId(user.getUserId());

        //Sort Chronologically
        transactions.sort(Comparator.comparing(Transaction::getID));

        //rebuild holdings from transactions
        portfolio.rebuildHoldingsfromTransactions(transactionRepo,stockRepo);

        //Update total value after all transactions
        portfolio.updateTotalValue(stockRepo);

        return portfolio;
    }
}
