
import CSVHandler.StockRepository;
import Domain.Stock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InMemoryStockRepository implements StockRepository {
    private final Map<String, Stock> stocks = new HashMap<>();

    private static String norm(String key) {
        return key == null ? null : key.toLowerCase(Locale.ROOT);
    }

    public void addStock(Stock stock) {
        stocks.put(norm(stock.getTicker()), stock);
    }

    @Override
    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks.values());
    }

    @Override
    public Stock getStockByTicker(String ticker) {
        return stocks.get(norm(ticker));
    }
}
