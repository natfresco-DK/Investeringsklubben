package CSVHandler;

import Domain.Stock;
import Domain.Transaction;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
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




}