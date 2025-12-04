package CSVHandler;

import Domain.Transaction;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import Domain.OrderType;
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
    public List<Transaction> getTransactionsByUserId(int userId) {
        String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";
        List<Transaction> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");

                try {
                    int csvUserId = Integer.parseInt(fields[1]);

                    if (csvUserId == userId) {
                        int id = Integer.parseInt(fields[0]);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        java.util.Date utilDate = sdf.parse(fields[2]); // kan kaste ParseException
                        Date date = new Date(utilDate.getTime());

                        String ticker = fields[3];
                        double price = Double.parseDouble(fields[4].replace(",", "."));
                        String currency = fields[5];
                        OrderType orderType = OrderType.valueOf(fields[6].toUpperCase());
                        int quantity = Integer.parseInt(fields[7]);

                        Transaction trx = new Transaction(id, csvUserId, date, ticker, price, currency, orderType, quantity);
                        result.add(trx);
                    }
                } catch (ParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // Log fejl og fortsæt til næste linje (så en korrupt linje ikke stopper hele læsningen)
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public void loadFromCSV(String filePath) {
        ArrayList<Object> transactions = new ArrayList<>(); // Clear existing transactions
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(";");
                if (fields.length < 8) continue;

                int id = Integer.parseInt(fields[0].trim());
                int userId = Integer.parseInt(fields[1].trim());
                java.util.Date utilDate = sdf.parse(fields[2].trim());
                Date date = new Date(utilDate.getTime());
                String ticker = fields[3].trim();
                double price = Double.parseDouble(fields[4].replace(",", ".").trim());
                String currency = fields[5].trim();
                OrderType orderType = OrderType.valueOf(fields[6].trim().toUpperCase());
                int quantity = Integer.parseInt(fields[7].trim());

                Transaction trx = new Transaction(id, userId, date, ticker, price, currency, orderType, quantity);
                transactions.add(trx);
            }
        } catch (Exception e) {
            System.out.println("Fejl ved læsning af CSV-fil: " + filePath);
            e.printStackTrace();
        }
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

