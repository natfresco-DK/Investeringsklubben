package Domain;

import java.util.*;

public class SortStocks {
    
    // Sorter TradingStatistics efter mest handlede (total antal trades)
    public static List<TradingStatistics> sortByMostTraded(List<TradingStatistics> statistics) {
        List<TradingStatistics> sorted = new ArrayList<>(statistics);
        sorted.sort((a, b) -> Integer.compare(b.getTotalTrades(), a.getTotalTrades()));
        return sorted;
    }
    
    // Sorter TradingStatistics efter mest solgte
    public static List<TradingStatistics> sortByMostSold(List<TradingStatistics> statistics) {
        List<TradingStatistics> sorted = new ArrayList<>(statistics);
        sorted.sort((a, b) -> Integer.compare(b.getTotalSells(), a.getTotalSells()));
        return sorted;
    }
    
    // Sorter TradingStatistics efter mest købte
    public static List<TradingStatistics> sortByMostBought(List<TradingStatistics> statistics) {
        List<TradingStatistics> sorted = new ArrayList<>(statistics);
        sorted.sort((a, b) -> Integer.compare(b.getTotalBuys(), a.getTotalBuys()));
        return sorted;
    }
    
    // Hent top N resultater fra en liste
    public static List<TradingStatistics> getTopN(List<TradingStatistics> statistics, int n) {
        if (n >= statistics.size()) {
            return new ArrayList<>(statistics);
        }
        return new ArrayList<>(statistics.subList(0, n));
    }
}
