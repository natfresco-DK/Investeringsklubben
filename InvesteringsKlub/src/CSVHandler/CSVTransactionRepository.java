package CSVHandler;

import Domain.Transaction;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import Domain.OrderType;
import java.text.SimpleDateFormat;


public class CSVTransactionRepository implements TransactionRepository {
    protected ArrayList<Transaction> transactions = new ArrayList<>();
    protected final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    protected final String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";

    public CSVTransactionRepository() {
        loadTransactions(filePath);
    }

    //writers
    public void writeTransaction(Transaction trx) {
        String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";
        File file = new File(filePath);
        boolean fileExists = file.exists();

        try (FileWriter writer = new FileWriter(file, true)) {
            // skriv header hvis filen ikke findes eller er tom
            if (!fileExists || file.length() == 0) {
                writer.append("ID;UserID;Date;Ticker;Price;Currency;Type;Quantity\n");
            }

            // Gem i korrekt CSV-format, ikke trx.toString()
            writer.append(trx.getID() + ";" +
                    trx.getUserID() + ";" +
                    new SimpleDateFormat("dd-MM-yyyy").format(trx.getDate()) + ";" +
                    trx.getTicker() + ";" +
                    trx.getPrice() + ";" +
                    trx.getCurrency() + ";" +
                    trx.getOrderType().name() + ";" +
                    trx.getQuantity() + "\n");
            transactions.add(trx);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //getters
    public int getNextTransactionId() {
        String filePath = "InvesteringsKlub/CSVRepository/transactions.csv";
        int nextId = 1; //Default if file is empty
        String lastLine = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String raw;
            while ((raw = br.readLine()) != null) {
                if (isSkippableLine(raw)) {
                    continue;
                }
                lastLine = raw;
            }
        } catch (IOException e) {
            //If file not found, empty or unreadable, we start from 1
            return nextId;
        }
        if (lastLine != null) {
            String[] parts = splitSemicolonTrim(lastLine);
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
            String raw;
            int lineNo = 0;
            while ((raw = br.readLine()) != null) {
                lineNo++;
                if (isSkippableLine(raw)) {
                    continue;
                }
                String[] fields = splitSemicolonTrim(raw);
                if (fields.length < 8) {
                    System.out.println("CSV warning. Too few coloumn at line " + lineNo + "===> '" + raw + "'");
                    continue;
                }
                try {
                    int csvUserId = Integer.parseInt(fields[1]);
                    if (csvUserId != userId) {
                        continue;
                    }
                    int id = Integer.parseInt(fields[0]);
                    java.util.Date utilDate = sdf.parse(fields[2]); // can caste ParseException
                    Date date = new Date(utilDate.getTime());
                    String ticker = fields[3];
                    double price = Double.parseDouble(fields[4].replace(",", "."));
                    String currency = fields[5];
                    OrderType orderType = OrderType.valueOf(fields[6].toUpperCase());
                    int quantity = Integer.parseInt(fields[7]);

                    result.add(new Transaction(id, csvUserId, date, ticker, price, currency, orderType, quantity));

                } catch (ParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("CSV warning at line " + lineNo + ": " + e.getMessage() + " -> '" + raw + "'");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public List<Transaction> getAllTransactions() {
        loadTransactions(filePath);
        return transactions;
    }

    /*public List<Transaction> getAllTransactions() {
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
            String raw;
            int lineNo = 0;
            while ((raw = br.readLine()) != null) {
                lineNo++;
                if (isSkippableLine(raw)) continue;

                String[] parts = splitSemicolonTrim(raw);
                if (parts.length < 8) {
                    System.err.println("CSV warning: too few columns at line " + lineNo + " -> '" + raw + "'");
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0]);
                    int userId = Integer.parseInt(parts[1]);

                    String dateStr = parts[2];
                    Date date;
                    try {
                        date = primarySdf.parse(dateStr);
                    } catch (ParseException e1) {
                        try { date = sdf.parse(dateStr); }
                        catch (ParseException e2) {
                            try { date = slashSdf.parse(dateStr); }
                            catch (ParseException e3) {
                                try { date = spaceSdf.parse(dateStr); }
                                catch (ParseException e4) {
                                    try { date = isoSdf.parse(dateStr); }
                                    catch (ParseException e5) {
                                        System.err.println("Failed to parse date at line " + lineNo + ": '" + dateStr + "' -> '" + raw + "'");
                                        continue;
                                    }
                                }
                            }
                        }
                    }

                    String ticker = parts[3];
                    double price = Double.parseDouble(parts[4].replace(",", "."));
                    String currency = parts[5];
                    Domain.OrderType orderType = Domain.OrderType.valueOf(parts[6].toUpperCase(Locale.ROOT));
                    int quantity = Integer.parseInt(parts[7]);

                    transactions.add(new Transaction(id, userId, date, ticker, price, currency, orderType, quantity));
                } catch (IllegalArgumentException e) {
                    System.err.println("CSV warning at line: " + lineNo + ": " + e.getMessage() + " -> '" + raw + "'");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;

    }*/

    //load transaction
    public void loadTransactions(String filePath) {
        transactions.clear(); // Clear existing transactions


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String raw;
            int lineNo = 0;
            while ((raw = br.readLine()) != null) {
                lineNo++;
                if (isSkippableLine(raw)) continue;

                String[] f = splitSemicolonTrim(raw);
                if (f.length < 8) {
                    System.err.println("CSV warning. too few columns at line " + lineNo + " -> '" + raw + "'");
                    continue;
                }
                try {
                    int id = Integer.parseInt(f[0]);
                    int userId = Integer.parseInt(f[1]);
                    java.util.Date utilDate = sdf.parse(f[2]);
                    Date date = new Date(utilDate.getTime());
                    String ticker = f[3];
                    double price = Double.parseDouble(f[4].replace(",", "."));
                    String currency = f[5];
                    OrderType orderType = OrderType.valueOf(f[6].toUpperCase(Locale.ROOT));
                    int quantity = Integer.parseInt(f[7]);

                    Transaction trx = new Transaction(id, userId, date, ticker, price, currency, orderType, quantity);
                    transactions.add(trx);
                } catch (Exception e) {
                    System.err.println("CSV warning at line " + lineNo + ": " + e.getMessage() + " ===> '" + raw + "'");
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading CSV file: " + filePath);
            e.printStackTrace();
        }
    }

    //helpers
    private boolean isSkippableLine(String raw) {
        if (raw == null) {
            return true;
        }
        String line = raw.trim();
        if (line.isEmpty()) {
            return true;
        }
        if (line.toLowerCase(Locale.ROOT).startsWith("id;")) {
            return true;
        } else {
            return false;
        }
    }

    private String[] splitSemicolonTrim(String line) {
        String[] parts = line.split(";");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }

}

