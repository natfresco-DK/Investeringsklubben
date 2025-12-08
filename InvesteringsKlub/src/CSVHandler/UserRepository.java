package CSVHandler;

import Domain.User;

import java.util.Collection;

public interface UserRepository {
    User getUserById(int id);
    Collection<User> getAllUsers();
    void addUsersPortfolio(StockRepository stockRepo, TransactionRepository transactionRepo);

}
