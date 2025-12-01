package CSVHandler;

import Domain.Stock;

import java.util.ArrayList;
import java.util.List;

public interface StockRepository {
    List<Stock> stocks = new ArrayList<>();
    void addStock(Stock stock);
    public void clear();
    List<Stock> getAllStocks();
    Stock getStockByTicker(String ticker);
}
