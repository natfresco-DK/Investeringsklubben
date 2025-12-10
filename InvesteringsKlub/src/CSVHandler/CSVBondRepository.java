package CSVHandler;

import Domain.Bond;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CSVBondRepository implements BondRepository {
    protected List<Bond> bonds = new ArrayList<>();
    protected final String filePath = "InvesteringsKlub/CSVRepository/bondMarket.csv";

    public CSVBondRepository() {
        loadBonds(filePath);
    }

    public List<Bond> getAllBonds() {
        loadBonds(filePath);
        return bonds;
    }

    public Bond getBondByTicker(String ticker) {
        loadBonds(filePath);
        for (Bond b : bonds) {
            if (ticker.equalsIgnoreCase(b.getTicker())) {
                return b;
            }
        }
        return null;
    }

    public void loadBonds(String filePath) {
        bonds.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String raw;
            int lineNo = 0;
            while ((raw = br.readLine()) != null) {
                lineNo++;
                if (isSkippableLine(raw)) continue;

                String[] f = splitSemicolonTrim(raw);
                if (f.length < 7) {
                    System.err.println("CSV warning. too few columns at line " + lineNo + " ==> '" + raw + "'");
                    continue;
                }

                try {
                    String ticker       = f[0];
                    String name         = f[1];
                    double price        = parsePrice(f[2]);
                    String currency     = f[3];
                    double couponRate   = parsePrice(f[4]);
                    String issueDate    = f[5];
                    String maturityDate = f[6];

                    Bond bond = new Bond(ticker, price, currency, name, couponRate, issueDate, maturityDate);
                    bonds.add(bond);
                } catch (Exception e) {
                    System.err.println("CSV warning. parse error at line " + lineNo + " ==> '" + raw + "'");
                }
            }
        } catch (Exception e) {
            System.err.println("CSV error. Could not read file: " + filePath);
        }
    }

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
