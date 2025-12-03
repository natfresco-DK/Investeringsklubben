import Domain.Transaction;
import java.util.ArrayList;
import java.util.List;
import CSVHandler.*;


public class InMemoryTransactionRepository implements TransactionRepository {
    private final List<Transaction> transactions = new ArrayList<>();
    private int nextId = 1;

    @Override
    public int getNextTransactionId() {
        return nextId++;
    }

    @Override
    public List<Transaction> readTransactionsByUserId(int userId) {
        return List.of();
    }

    @Override
    public void writeTransaction(Transaction trx) {
        transactions.add(trx);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void clear() {
        transactions.clear();
        nextId = 1;
    }
}
