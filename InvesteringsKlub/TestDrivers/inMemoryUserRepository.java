import Builder.PortfolioBuilder;
import CSVHandler.BondRepository;
import CSVHandler.StockRepository;
import CSVHandler.TransactionRepository;
import Domain.Portfolio;
import Domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.*;

public class inMemoryUserRepository implements CSVHandler.UserRepository {

    private final Map<Integer, User> users = new LinkedHashMap<>();

    @Override
    public void addUser(User u) { users.put(u.getUserId(), u); }

    @Override
    public User getUserById(int id) { return users.get(id); }

    @Override
    public List<User> getAllUsers() { return new ArrayList<>(users.values()); }

    @Override
    public void addUsersPortfolio(StockRepository stockRepo, BondRepository bondRepo, TransactionRepository transactionRepo) {
        for (User u : users.values()) {
            Portfolio p = PortfolioBuilder.buildPortfolio(u, (InMemoryStockRepository)stockRepo, (InMemoryBondRepository)bondRepo, transactionRepo);
            u.setPortfolio(p);
        }
    }
}