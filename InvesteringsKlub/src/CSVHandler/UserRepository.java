package CSVHandler;

import Domain.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository {
    User getUserById(int id);
    List<User> getAllUsers();
    void addUsersPortfolio(StockRepository stockRepo, BondRepository bondRepo, TransactionRepository transactionRepo);
    void addUser(User user);

}
