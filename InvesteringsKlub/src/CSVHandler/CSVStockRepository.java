
package CSVHandler;

import Domain.Stock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CSVStockRepository implements StockRepository {
    protected List<Stock> stocks = new ArrayList<>();
    protected final String filePath = "InvesteringsKlub/CSVRepository/stockMarket.csv";

    public CSVStockRepository() {
        loadStocks(filePath);
    }

    // Getters
    public List<Stock> getAllStocks() {
        loadStocks(filePath);
        return stocks;
    }

    public Stock getStockByTicker(String ticker) {
        loadStocks(filePath);
        for (Stock s : stocks) {
            if (ticker.equalsIgnoreCase(s.getTicker())) {
                return s;
            }
        }
        return null;
    }

    // Load stocks
    private void loadStocks(String filePath) {
        stocks.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String raw;
            int lineNo = 0;
            while ((raw = br.readLine()) != null) {
                lineNo++;
                if (isSkippableLine(raw)) continue;

                String[] f = splitSemicolonTrim(raw);
                if (f.length < 5) {
                    System.err.println("CSV warning. too few columns at line " + lineNo + " ==> '" + raw + "'");
                    continue;
                }

                try {
                    String ticker   = f[0];
                    String name     = f[1];
                    String sector   = f[2];
                    double price    = parsePrice(f[3]); // handles comma→dot
                    String currency = f[4];

                    Stock stock = new Stock(ticker, price, currency, name, sector);
                    stocks.add(stock);
                } catch (Exception e) {
                    System.err.println("CSV warning at line " + lineNo + ": " + e.getMessage() + " ===> '" + raw + "'");
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading CSV file: " + filePath);
            e.printStackTrace();
        }
    }

    // helpers
    private boolean isSkippableLine(String raw) {
        if (raw == null) return true;
        String line = raw.trim();
        if (line.isEmpty()) return true;
        return line.toLowerCase(Locale.ROOT).startsWith("ticker;");
    }

    private String[] splitSemicolonTrim(String line) {
        String[] parts = line.split(";");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    private double parsePrice(String value) {
        if (value == null) return 0.0;
        String normalized = value.trim().replace(",", ".");
        if (normalized.isEmpty()) return 0.0;
        return Double.parseDouble(normalized);
    }

}
