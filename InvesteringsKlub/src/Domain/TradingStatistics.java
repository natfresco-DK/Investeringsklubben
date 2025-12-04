package Domain;

import CSVHandler.*;
import java.util.*;

public class TradingStatistics {
    private String ticker;
    private String stockName;
    private Sektor sektor;
    private int totalBuys;
    private int totalSells;
    private int totalTrades;
    private double currentPrice;

    public TradingStatistics(String ticker, String stockName, Sektor sektor) {
        this.ticker = ticker;
        this.stockName = stockName;
        this.sektor = sektor;
        this.totalBuys = 0;
        this.totalSells = 0;
        this.totalTrades = 0;
    }

    // Getters
    public String getTicker() { return ticker; }
    public String getStockName() { return stockName; }
    public Sektor getSektor() { return sektor; }
    public int getTotalBuys() { return totalBuys; }
    public int getTotalSells() { return totalSells; }
    public int getTotalTrades() { return totalTrades; }
    public double getCurrentPrice() { return currentPrice; }

    // Setters
    public void setTotalBuys(int totalBuys) { this.totalBuys = totalBuys; }
    public void setTotalSells(int totalSells) { this.totalSells = totalSells; }
    public void setTotalTrades(int totalTrades) { this.totalTrades = totalTrades; }

    // Increment methods
    public void incrementBuys(int quantity) { 
        this.totalBuys += quantity; 
        this.totalTrades++;
    }
    
    public void incrementSells(int quantity) { 
        this.totalSells += quantity; 
        this.totalTrades++;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s: %d køb, %d salg", 
            ticker, stockName, sektor.getDisplayName(), totalBuys, totalSells);
    }

    // Beregn trading statistics fra transaktioner
    public static List<TradingStatistics> calculateFromTransactions(
            List<Transaction> transactions, 
            StockRepository stockRepo) {
        
        Map<String, TradingStatistics> statsMap = new HashMap<>();
        
        for (Transaction trx : transactions) {
            String ticker = trx.getTicker();
            
            // Hent eller opret TradingStatistics for denne aktie
            TradingStatistics stats = statsMap.get(ticker);
            
            if (stats == null) {
                Stock stock = stockRepo.getStockByTicker(ticker);
                if (stock != null) {
                    try {
                        Sektor sektor = Sektor.fromString(stock.getSector());
                        stats = new TradingStatistics(ticker, stock.getName(), sektor);
                        statsMap.put(ticker, stats);
                    } catch (IllegalArgumentException e) {
                        // Skip stocks med ukendt sektor
                        continue;
                    }
                }
            }
            
            // Opdater statistik baseret på transaction type
            if (stats != null) {
                if (trx.getOrderType() == OrderType.BUY) {
                    stats.incrementBuys(trx.getQuantity());
                } else if (trx.getOrderType() == OrderType.SELL) {
                    stats.incrementSells(trx.getQuantity());
                }
            }
        }
        
        return new ArrayList<>(statsMap.values());
    }

    // Filtrer statistics efter sektor
    public static List<TradingStatistics> filterBySektor(
            List<TradingStatistics> statistics, 
            Sektor sektor) {
        
        List<TradingStatistics> filtered = new ArrayList<>();
        for (TradingStatistics stats : statistics) {
            if (stats.getSektor() == sektor) {
                filtered.add(stats);
            }
        }
        return filtered;
    }
}

