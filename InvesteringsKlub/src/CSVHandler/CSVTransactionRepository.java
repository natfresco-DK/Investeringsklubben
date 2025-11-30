package CSVHandler;

import Domain.Transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
}