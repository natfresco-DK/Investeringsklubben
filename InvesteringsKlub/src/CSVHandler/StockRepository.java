package CSVHandler;

import Domain.Stock;

import java.util.ArrayList;
import java.util.List;

public interface StockRepository {
    List<Stock> getAllStocks();
    Stock getStockByTicker(String ticker);

}
