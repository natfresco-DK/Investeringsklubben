package Domain;

import Domain.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVUserRepository {

    private Map<Integer, User> users = new HashMap<>();
    private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public CSVUserRepository(String filePath) {
        loadUsers(filePath);
    }

    private void loadUsers(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");

                int id = Integer.parseInt(fields[0]);
                String fullName = fields[1];
                String email = fields[2];
                Date birth = df.parse(fields[3]);
                int initialCash = Integer.parseInt(fields[4]);
                Date created = df.parse(fields[5]);
                Date updated = df.parse(fields[6]);

                User u = new User(id, fullName, email, birth, initialCash, created, updated);
                users.put(id, u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//jkk

    public User getUserById(int id) {
        return users.get(id);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}
