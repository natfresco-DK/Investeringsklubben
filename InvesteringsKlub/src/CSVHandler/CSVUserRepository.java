package CSVHandler;

import Domain.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVUserRepository {
    public static List<User> loadUsers(String filePath) {
        List<User> users = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");

                int userId = Integer.parseInt(fields[0]);
                String fullName = fields[1];
                String email = fields[2];

                Date birthDate = sdf.parse(fields[3]);
                int initialCash = Integer.parseInt(fields[4]);

                Date createdAt = sdf.parse(fields[5]);
                Date lastUpdated = sdf.parse(fields[6]);

                User user = new User(
                        userId,
                        fullName,
                        email,
                        birthDate,
                        initialCash,
                        createdAt,
                        lastUpdated,
                        false // no auto building of portfolio so portfoliobuilder can make the right portfolio later
                );

                users.add(user);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return users;
    }
}


