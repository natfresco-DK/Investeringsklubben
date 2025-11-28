import java.io.*;
import java.util.*;

public class PortfolioBuilder {

    public static Map<String, Holding> buildPortfolio(String userId, String transactionsFile, StockRepository stockRepo) {
        Map<String, Holding> portfolio = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(transactionsFile))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String txUserId = parts[0];
                if (!txUserId.equals(userId)) continue;

                String ticker = parts[2];
                double price = Double.parseDouble(parts[3]);
                String orderType = parts[5]; // "BUY" or "SELL"
                int quantity = Integer.parseInt(parts[6]);

                Holding holding = portfolio.getOrDefault(ticker, new Holding(ticker, 0, 0, 0));

                if (orderType.equalsIgnoreCase("BUY")) {
                    // Update quantity and weighted purchase price
                    double totalCost = holding.getQuantity() * holding.getPurchasePriceDKK() + quantity * price;
                    int newQuantity = holding.getQuantity() + quantity;
                    double newPurchasePrice = newQuantity > 0 ? totalCost / newQuantity : 0;

                    holding.setQuantity(newQuantity);
                    holding.setPurchasePriceDKK(newPurchasePrice);
                } else if (orderType.equalsIgnoreCase("SELL")) {
                    holding.setQuantity(Math.max(0, holding.getQuantity() - quantity));
                }

                // Always update current price from StockRepository
                double currentPrice = CSVStockRepository.getStockByTicker(ticker).getPrice();
                holding.setCurrentPriceDKK(currentPrice);

                portfolio.put(ticker, holding);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return portfolio;
    }
}
