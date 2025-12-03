package CSVHandler;

import Domain.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class CSVTransactionRepository implements TransactionRepository {
    public void writeTransaction(Transaction trx) {
        String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append(trx.toString() + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNextTransactionId() {
        String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";
        int nextId = 1; //Default if file is empty
        String lastLine = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lastLine = line;
                }
            }
        } catch (IOException e) {
            //If file not found or unreadable, we start from 1
            return nextId;
        }
        if (lastLine != null) {
            //Optional: detect & skip header row
            if (lastLine.toLowerCase().startsWith("id;")) {
                return nextId;
            }
            String[] parts = lastLine.split(";");
            if (parts.length > 0) {
                try {
                    int lastId = Integer.parseInt(parts[0].trim());
                    nextId = lastId + 1;
                } catch (NumberFormatException ignored) {
                    //Malformed last line; keep default nextId = 1
                }
            }
        }
        return nextId;
    }
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";
        // Primary parser matches Date.toString() like: Tue Dec 02 23:24:40 CET 2025
        SimpleDateFormat primarySdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        // Add common CSV date formats as fallbacks
        SimpleDateFormat dashSdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat slashSdf = new SimpleDateFormat("dd/MM/yyyy");
        // Older code used space-separated day month year
        SimpleDateFormat spaceSdf = new SimpleDateFormat("dd MM yyyy");
        // ISO with timezone
        SimpleDateFormat isoSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.toLowerCase().startsWith("id;")) continue; // Spring header eller tomme linjer

                String[] parts = line.split(";");
                if (parts.length == 8) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        int userId = Integer.parseInt(parts[1].trim());
                        Date date;
                        String dateStr = parts[2].trim();
                        // Try primary format first, then several fallbacks
                        try {
                            date = primarySdf.parse(dateStr);
                        } catch (ParseException e1) {
                            try {
                                date = dashSdf.parse(dateStr);
                            } catch (ParseException e2) {
                                try {
                                    date = slashSdf.parse(dateStr);
                                } catch (ParseException e3) {
                                    try {
                                        date = spaceSdf.parse(dateStr);
                                    } catch (ParseException e4) {
                                        try {
                                            date = isoSdf.parse(dateStr);
                                        } catch (ParseException e5) {
                                            // Couldn't parse date - log and skip this line
                                            System.err.println("Failed to parse date for transaction id " + parts[0].trim() + ": '" + dateStr + "'");
                                            continue;
                                        }
                                    }
                                }
                            }
                        }

                        String ticker = parts[3].trim();
                        double price = Double.parseDouble(parts[4].trim().replace(",", "."));
                        String currency = parts[5].trim();
                        Domain.OrderType orderType = Domain.OrderType.valueOf(parts[6].trim().toUpperCase());
                        int quantity = Integer.parseInt(parts[7].trim());

                        Transaction trx = new Transaction(id, userId, date, ticker, price, currency, orderType, quantity);
                        transactions.add(trx);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace(); // Fanger NumberFormatException ogsÃ¥
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
