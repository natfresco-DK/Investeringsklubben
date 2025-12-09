
package CSVHandler;

import Builder.PortfolioBuilder;
import Domain.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVUserRepository implements UserRepository {
    private Map<Integer, User> users = new HashMap<>();
    
    private static String getCSVPath(String filename) {
        // Prøv først CSVRepository/ (VS Code working directory)
        File file = new File("CSVRepository/" + filename);
        if (file.exists()) {
            return "CSVRepository/" + filename;
        }
        // Ellers brug InvesteringsKlub/CSVRepository/ (IntelliJ working directory)
        return "InvesteringsKlub/CSVRepository/" + filename;
    }
    
    private final String filePath = getCSVPath("users.csv");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public CSVUserRepository() {
        loadUsers(filePath);
    }

    //Getters
    public User getUserById(int id) {
        return users.get(id);
    }
    public Collection<User> getAllUsers() {
        loadUsers(filePath);
        return users.values();
    }

    //Load users
    private void loadUsers(String path) {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String raw;
            int lineNo = 0;
            while ((raw = br.readLine()) != null) {
                lineNo++;
                if (isSkippableLine(raw)) continue;

                String[] f = splitSemicolonTrim(raw);
                // Expect: user_id;full_name;email;birth;initial_cash;created;updated  => 7 columns
                if (f.length < 7) {
                    System.err.println("CSV warning: too few columns at line " + lineNo + " -> '" + raw + "'");
                    continue;
                }
                try {
                    int id           = Integer.parseInt(f[0]);
                    String fullName  = f[1];
                    String email     = f[2];
                    Date birth       = parseDateOrWarn(f[3], "birth", lineNo, raw);
                    int initialCash  = Integer.parseInt(f[4]);
                    Date created     = parseDateOrWarn(f[5], "created", lineNo, raw);
                    Date updated     = parseDateOrWarn(f[6], "updated", lineNo, raw);

                    // If any mandatory date failed, skip the record (already logged)
                    if (birth == null || created == null || updated == null) continue;

                    User u = new User(id, fullName, email, birth, initialCash, created, updated);
                    users.put(id, u);

                } catch (NumberFormatException e) {
                    System.err.println("CSV warning at line " + lineNo + ": " + e.getMessage() + " -> '" + raw + "'");
                } catch (Exception e) {
                    // Catch any unexpected runtime issues for this line, keep going
                    System.err.println("CSV warning unexpected error at line " + lineNo + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + path);
            e.printStackTrace();
        }
    }

    // Add portfolio to user
    public void addUsersPortfolio(StockRepository stockRepo, TransactionRepository transactionRepo) {
        for (User user : getAllUsers()) {
            user.setPortfolio(PortfolioBuilder.buildPortfolio(user, stockRepo, transactionRepo));
        }
    }

    // Helpers
    private boolean isSkippableLine(String raw) {
        if (raw == null){
            return true;
        }
        String line = raw.trim();
        if (line.isEmpty()){
            return true;
        }
        if (line.toLowerCase(Locale.ROOT).startsWith("user_id;")) {
            return true;
        }
        return false;
    }
    private String[] splitSemicolonTrim(String line) {
        String[] parts = line.split(";");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }
    private Date parseDateOrWarn(String value, String label, int lineNo, String rawLine) {
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            System.err.println("CSV warning: failed to parse " + label + " date at line "
                    + lineNo + ": '" + value + "' -> '" + rawLine + "'");
            return null;
        }
    }
}
