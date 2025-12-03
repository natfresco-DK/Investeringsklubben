package CSVHandler;

import Domain.Stock;

import java.util.ArrayList;
import java.util.List;

public interface StockRepository {

    void addStock(Stock stock);
    void clear();
    List<Stock> getAllStocks();
    Stock getStockByTicker(String ticker);
    void loadFromCSV(String filePath);

}
