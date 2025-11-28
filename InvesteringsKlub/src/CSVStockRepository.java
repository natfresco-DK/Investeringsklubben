import java.util.*;

public class CSVStockRepository implements StockRepository {
    protected static List<Stock> stocks = new ArrayList<>();

    @Override
    public void getStocks() {
        readStocks();
    }

    public static void readStocks() {

    }

    public static Stock getStockByTicker(String ticker){
        Stock stock = new Stock();
        for (Stock s : stocks){
            if(ticker == s.getTicker())
                stock = s;
        }
        return stock;
    }
}
