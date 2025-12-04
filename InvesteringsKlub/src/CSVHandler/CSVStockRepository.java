package CSVHandler;
import Domain.Stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CSVStockRepository implements StockRepository {
    protected List<Stock> stocks = new ArrayList<>();

    public CSVStockRepository(){}

    public void addStock(Stock stock){
        stocks.add(stock);
    }

    public void clear(){
        stocks.clear();
    }

    public List<Stock> getAllStocks() {
        return new ArrayList<>(stocks);
    }

    /*
    public Stock getStockByTicker(String ticker){
        Stock stock = new Stock();
        for (Stock s : stocks){
            if(ticker == s.getTicker())
                stock = s;
        }
        return stock;
    }

     */
    public Stock getStockByTicker(String ticker) {
        if (ticker == null) return null;
        for (Stock s : stocks) {
            if (ticker.equalsIgnoreCase(s.getTicker())) {
                return s;
            }
        }
        return null; // ikke fundet
    }

    public void loadFromCSV(String filePath) {
        clear();

        try (BufferedReader reader = Files.newBufferedReader(
                Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue;

                String ticker   = parts[0].trim();
                String name     = parts[1].trim();
                String sector   = parts[2].trim();
                String priceStr = parts[3].trim();
                String currency = parts[4].trim();

                double price = parseDanishDouble(priceStr);

                Stock stock = new Stock(ticker, price, currency, name, sector);
                addStock(stock);
            }

        } catch (IOException e) {
            System.out.println("Fejl ved lÃ¦sning af CSV-fil: " + filePath);
            e.printStackTrace();
        }
    }

    private double parseDanishDouble(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        value = value.replace(",", ".");
        return Double.parseDouble(value);
    }



}
