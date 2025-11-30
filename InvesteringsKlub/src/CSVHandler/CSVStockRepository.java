package CSVHandler;
import Domain.Stock;

import java.util.*;

public class CSVStockRepository implements StockRepository {
    protected List<Stock> stocks = new ArrayList<>();

    public CSVStockRepository(){}

    public CSVStockRepository(List<Stock> stocks){
        this.stocks = stocks;
    }

    public void addStock(Stock stock){
        stocks.add(stock);
    }

    public void clear(){
        stocks.clear();
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks);
    }

    public Stock getStockByTicker(String ticker){
        Stock stock = new Stock();
        for (Stock s : stocks){
            if(ticker == s.getTicker())
                stock = s;
        }
        return stock;
    }
}
