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

        try (BufferedReader br = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String txUserId = parts[0];
                if (!txUserId.equals(String.valueOf(user.getUserId()))) continue;

                String ticker = parts[2];
                double price = Double.parseDouble(parts[3]);
                String orderType = parts[5]; // BUY or SELL
                int quantity = Integer.parseInt(parts[6]);

                if (orderType.equalsIgnoreCase("BUY")) {
                    portfolio.buyStock(ticker, quantity, stockRepo, transactionRepo);
                } else if (orderType.equalsIgnoreCase("SELL")) {
                    portfolio.sellStock(ticker, quantity, stockRepo, transactionRepo);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update total value after all transactions
        portfolio.updateTotalValue(stockRepo);
        return portfolio;
    }
}
