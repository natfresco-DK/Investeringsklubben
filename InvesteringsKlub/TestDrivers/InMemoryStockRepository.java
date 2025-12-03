import CSVHandler.CSVStockRepository;
import CSVHandler.StockRepository;
import Domain.Stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStockRepository implements StockRepository {

    private final Map<String, Stock> stocks = new HashMap<>();

    public void addStock(Stock stock) {
        stocks.put(stock.getTicker(), stock);
    }

    @Override
    public void clear() {
    }

    @Override
    public List<Stock> getAllStocks() {
        return List.of();
    }

    @Override
    public Stock getStockByTicker(String ticker) {
        return stocks.get(ticker);
    }

    public void loadFromCSV(String filePath) {
        CSVStockRepository repo = new CSVStockRepository();
    }
}