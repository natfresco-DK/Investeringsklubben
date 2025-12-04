import Domain.*;
import CSVHandler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SektorAndTradingStatisticsTest {

    private Sektor healthCare;
    private Sektor technology;
    private TradingStatistics appleStats;
    private TradingStatistics googleStats;
    private List<Stock> testStocks;
    private InMemoryStockRepository stockRepo;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setup() {
        healthCare = Sektor.HEALTH_CARE;
        technology = Sektor.TECHNOLOGY;
        
        appleStats = new TradingStatistics("AAPL", "Apple Inc.", technology);
        googleStats = new TradingStatistics("GOOG", "Google Inc.", technology);
        
        // Setup test stocks
        stockRepo = new InMemoryStockRepository();
        testStocks = new ArrayList<>();
        
        testStocks.add(new Stock("AAPL", 150.0, "DKK", "Apple Inc.", "Technology"));
        testStocks.add(new Stock("GOOG", 2800.0, "DKK", "Google Inc.", "Technology"));
        testStocks.add(new Stock("JNJ", 180.0, "DKK", "Johnson & Johnson", "Health Care"));
        testStocks.add(new Stock("PFE", 45.0, "DKK", "Pfizer", "Health Care"));
        testStocks.add(new Stock("XOM", 110.0, "DKK", "Exxon Mobil", "Energy"));
        
        for (Stock stock : testStocks) {
            stockRepo.addStock(stock);
        }
        
        // Setup test transactions
        testTransactions = new ArrayList<>();
        testTransactions.add(new Transaction(1, 1, new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, 10));
        testTransactions.add(new Transaction(2, 1, new Date(), "AAPL", 150.0, "DKK", OrderType.BUY, 5));
        testTransactions.add(new Transaction(3, 1, new Date(), "AAPL", 155.0, "DKK", OrderType.SELL, 3));
        testTransactions.add(new Transaction(4, 2, new Date(), "GOOG", 2800.0, "DKK", OrderType.BUY, 2));
        testTransactions.add(new Transaction(5, 2, new Date(), "JNJ", 180.0, "DKK", OrderType.BUY, 8));
        testTransactions.add(new Transaction(6, 2, new Date(), "JNJ", 182.0, "DKK", OrderType.SELL, 4));
    }

    // Sektor Tests
    @Test
    void testSektorGetDisplayName() {
        assertEquals("Health Care", healthCare.getDisplayName());
        assertEquals("Technology", technology.getDisplayName());
    }

    @Test
    void testSektorFromString() {
        Sektor result = Sektor.fromString("Health Care");
        assertEquals(Sektor.HEALTH_CARE, result);
        
        result = Sektor.fromString("technology");
        assertEquals(Sektor.TECHNOLOGY, result);
    }

    @Test
    void testSektorFromStringInvalidSector() {
        assertThrows(IllegalArgumentException.class, () -> {
            Sektor.fromString("Invalid Sector");
        });
    }

    @Test
    void testSektorAllValues() {
        Sektor[] sectors = Sektor.values();
        assertEquals(7, sectors.length);
        
        assertTrue(containsSektor(sectors, Sektor.HEALTH_CARE));
        assertTrue(containsSektor(sectors, Sektor.INDUSTRIALS));
        assertTrue(containsSektor(sectors, Sektor.CONSUMER_GOODS));
        assertTrue(containsSektor(sectors, Sektor.FINANCIALS));
        assertTrue(containsSektor(sectors, Sektor.ENERGY));
        assertTrue(containsSektor(sectors, Sektor.UTILITIES));
        assertTrue(containsSektor(sectors, Sektor.TECHNOLOGY));
    }

    private boolean containsSektor(Sektor[] sectors, Sektor target) {
        for (Sektor s : sectors) {
            if (s == target) return true;
        }
        return false;
    }

    // TradingStatistics Tests
    @Test
    void testTradingStatisticsCreation() {
        assertEquals("AAPL", appleStats.getTicker());
        assertEquals("Apple Inc.", appleStats.getStockName());
        assertEquals(Sektor.TECHNOLOGY, appleStats.getSektor());
        assertEquals(0, appleStats.getTotalBuys());
        assertEquals(0, appleStats.getTotalSells());
        assertEquals(0, appleStats.getTotalTrades());
    }

    @Test
    void testSetTotalBuys() {
        appleStats.setTotalBuys(10);
        assertEquals(10, appleStats.getTotalBuys());
    }

    @Test
    void testSetTotalSells() {
        appleStats.setTotalSells(5);
        assertEquals(5, appleStats.getTotalSells());
    }

    @Test
    void testTradingStatisticsMultipleOperations() {
        appleStats.setTotalBuys(15);
        appleStats.setTotalSells(8);
        
        assertEquals(15, appleStats.getTotalBuys());
        assertEquals(8, appleStats.getTotalSells());
    }

    @Test
    void testTradingStatisticsToString() {
        appleStats.setTotalBuys(10);
        appleStats.setTotalSells(5);
        
        String result = appleStats.toString();
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("Apple Inc."));
        assertTrue(result.contains("Technology"));
        assertTrue(result.contains("10 køb"));
        assertTrue(result.contains("5 salg"));
    }

    @Test
    void testMultipleTradingStatisticsWithSameSektor() {
        assertEquals(appleStats.getSektor(), googleStats.getSektor());
        assertEquals("Technology", appleStats.getSektor().getDisplayName());
        assertEquals("Technology", googleStats.getSektor().getDisplayName());
    }

    @Test
    void testTradingStatisticsWithDifferentSektors() {
        TradingStatistics healthStats = new TradingStatistics(
            "JNJ", 
            "Johnson & Johnson", 
            Sektor.HEALTH_CARE
        );
        
        assertNotEquals(appleStats.getSektor(), healthStats.getSektor());
        assertEquals("Technology", appleStats.getSektor().getDisplayName());
        assertEquals("Health Care", healthStats.getSektor().getDisplayName());
    }

    @Test
    void testTradingStatisticsGetCurrentPrice() {
        assertEquals(0.0, appleStats.getCurrentPrice());
    }

    @Test
    void testTradingStatisticsZeroValues() {
        TradingStatistics newStats = new TradingStatistics("MSFT", "Microsoft", Sektor.TECHNOLOGY);
        
        assertEquals(0, newStats.getTotalBuys());
        assertEquals(0, newStats.getTotalSells());
        assertEquals(0, newStats.getTotalTrades());
        assertEquals(0.0, newStats.getCurrentPrice());
    }

    @Test
    void testSektorCaseInsensitiveFromString() {
        assertEquals(Sektor.HEALTH_CARE, Sektor.fromString("health care"));
        assertEquals(Sektor.HEALTH_CARE, Sektor.fromString("HEALTH CARE"));
        assertEquals(Sektor.HEALTH_CARE, Sektor.fromString("Health Care"));
    }

    // SectorFilter Tests
    @Test
    void testFilterBySektor() {
        List<Stock> techStocks = SectorFilter.filterBySektor(testStocks, Sektor.TECHNOLOGY);
        assertEquals(2, techStocks.size());
        assertTrue(containsStockWithTicker(techStocks, "AAPL"));
        assertTrue(containsStockWithTicker(techStocks, "GOOG"));
    }

    @Test
    void testFilterBySektorHealthCare() {
        List<Stock> healthStocks = SectorFilter.filterBySektor(testStocks, Sektor.HEALTH_CARE);
        assertEquals(2, healthStocks.size());
        assertTrue(containsStockWithTicker(healthStocks, "JNJ"));
        assertTrue(containsStockWithTicker(healthStocks, "PFE"));
    }

    @Test
    void testFilterBySektorEnergy() {
        List<Stock> energyStocks = SectorFilter.filterBySektor(testStocks, Sektor.ENERGY);
        assertEquals(1, energyStocks.size());
        assertTrue(containsStockWithTicker(energyStocks, "XOM"));
    }

    @Test
    void testFilterBySektorEmptyResult() {
        List<Stock> industrialStocks = SectorFilter.filterBySektor(testStocks, Sektor.INDUSTRIALS);
        assertEquals(0, industrialStocks.size());
    }

    @Test
    void testGroupBySektor() {
        Map<Sektor, List<Stock>> grouped = SectorFilter.groupBySektor(testStocks);
        
        assertEquals(2, grouped.get(Sektor.TECHNOLOGY).size());
        assertEquals(2, grouped.get(Sektor.HEALTH_CARE).size());
        assertEquals(1, grouped.get(Sektor.ENERGY).size());
        assertEquals(0, grouped.get(Sektor.INDUSTRIALS).size());
    }

    @Test
    void testGroupBySektorAllSektorsPresent() {
        Map<Sektor, List<Stock>> grouped = SectorFilter.groupBySektor(testStocks);
        
        // Verify all sectors are in the map
        for (Sektor sektor : Sektor.values()) {
            assertTrue(grouped.containsKey(sektor));
            assertNotNull(grouped.get(sektor));
        }
    }

    private boolean containsStockWithTicker(List<Stock> stocks, String ticker) {
        for (Stock stock : stocks) {
            if (stock.getTicker().equals(ticker)) {
                return true;
            }
        }
        return false;
    }

    // TradingStatistics Calculation Tests
    @Test
    void testCalculateFromTransactions() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        assertEquals(3, stats.size()); // AAPL, GOOG, JNJ
    }

    @Test
    void testCalculateFromTransactionsAppleStats() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        TradingStatistics appleStats = findStatsByTicker(stats, "AAPL");
        assertNotNull(appleStats);
        assertEquals(15, appleStats.getTotalBuys()); // 10 + 5
        assertEquals(3, appleStats.getTotalSells());
        assertEquals(3, appleStats.getTotalTrades());
    }

    @Test
    void testCalculateFromTransactionsJohnsonStats() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        TradingStatistics jnjStats = findStatsByTicker(stats, "JNJ");
        assertNotNull(jnjStats);
        assertEquals(8, jnjStats.getTotalBuys());
        assertEquals(4, jnjStats.getTotalSells());
        assertEquals(2, jnjStats.getTotalTrades());
    }

    @Test
    void testIncrementBuys() {
        appleStats.incrementBuys(10);
        assertEquals(10, appleStats.getTotalBuys());
        assertEquals(1, appleStats.getTotalTrades());
        
        appleStats.incrementBuys(5);
        assertEquals(15, appleStats.getTotalBuys());
        assertEquals(2, appleStats.getTotalTrades());
    }

    @Test
    void testIncrementSells() {
        appleStats.incrementSells(7);
        assertEquals(7, appleStats.getTotalSells());
        assertEquals(1, appleStats.getTotalTrades());
    }

    @Test
    void testFilterStatsBySektor() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> techStats = TradingStatistics.filterBySektor(
            stats, Sektor.TECHNOLOGY
        );
        
        assertEquals(2, techStats.size());
        assertNotNull(findStatsByTicker(techStats, "AAPL"));
        assertNotNull(findStatsByTicker(techStats, "GOOG"));
    }

    @Test
    void testFilterStatsBySektorHealthCare() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> healthStats = TradingStatistics.filterBySektor(
            stats, Sektor.HEALTH_CARE
        );
        
        assertEquals(1, healthStats.size());
        assertNotNull(findStatsByTicker(healthStats, "JNJ"));
    }

    // SortStocks Tests
    @Test
    void testSortByMostTraded() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> sorted = SortStocks.sortByMostTraded(stats);
        
        // AAPL har 3 trades, JNJ har 2, GOOG har 1
        assertEquals("AAPL", sorted.get(0).getTicker());
        assertEquals("JNJ", sorted.get(1).getTicker());
        assertEquals("GOOG", sorted.get(2).getTicker());
    }

    @Test
    void testSortByMostSold() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> sorted = SortStocks.sortByMostSold(stats);
        
        // JNJ: 4 solgt, AAPL: 3 solgt, GOOG: 0 solgt
        assertEquals("JNJ", sorted.get(0).getTicker());
        assertEquals("AAPL", sorted.get(1).getTicker());
        assertEquals("GOOG", sorted.get(2).getTicker());
    }

    @Test
    void testSortByMostBought() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> sorted = SortStocks.sortByMostBought(stats);
        
        // AAPL: 15 købt, JNJ: 8 købt, GOOG: 2 købt
        assertEquals("AAPL", sorted.get(0).getTicker());
        assertEquals("JNJ", sorted.get(1).getTicker());
        assertEquals("GOOG", sorted.get(2).getTicker());
    }

    @Test
    void testGetTopN() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> sorted = SortStocks.sortByMostTraded(stats);
        List<TradingStatistics> top2 = SortStocks.getTopN(sorted, 2);
        
        assertEquals(2, top2.size());
        assertEquals("AAPL", top2.get(0).getTicker());
        assertEquals("JNJ", top2.get(1).getTicker());
    }

    @Test
    void testGetTopNMoreThanAvailable() {
        List<TradingStatistics> stats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> top10 = SortStocks.getTopN(stats, 10);
        
        assertEquals(3, top10.size()); // Kun 3 stocks i alt
    }

    // Integration Test - Complete User Story 8 Workflow
    @Test
    void testCompleteUserStory8Workflow() {
        // 1. Beregn statistik fra transaktioner
        List<TradingStatistics> allStats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        // 2. Filtrer efter Technology sektor
        List<TradingStatistics> techStats = TradingStatistics.filterBySektor(
            allStats, Sektor.TECHNOLOGY
        );
        
        // 3. Sorter efter mest solgte
        List<TradingStatistics> sortedByMostSold = SortStocks.sortByMostSold(techStats);
        
        // 4. Hent top 5
        List<TradingStatistics> top5 = SortStocks.getTopN(sortedByMostSold, 5);
        
        // Verify resultater
        assertEquals(2, top5.size()); // Kun 2 tech stocks
        assertEquals("AAPL", top5.get(0).getTicker());
        assertEquals(3, top5.get(0).getTotalSells());
    }

    @Test
    void testCompleteWorkflowAllSectors() {
        // Test hele workflow uden sektor-filtrering
        List<TradingStatistics> allStats = TradingStatistics.calculateFromTransactions(
            testTransactions, stockRepo
        );
        
        List<TradingStatistics> sorted = SortStocks.sortByMostBought(allStats);
        List<TradingStatistics> top3 = SortStocks.getTopN(sorted, 3);
        
        assertEquals(3, top3.size());
        assertEquals("AAPL", top3.get(0).getTicker());
        assertEquals(15, top3.get(0).getTotalBuys());
    }

    private TradingStatistics findStatsByTicker(List<TradingStatistics> stats, String ticker) {
        for (TradingStatistics stat : stats) {
            if (stat.getTicker().equals(ticker)) {
                return stat;
            }
        }
        return null;
    }
}
