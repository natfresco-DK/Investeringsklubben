package CSVHandler;

import Domain.Transaction;
import java.util.List;

public interface TransactionRepository {
    void writeTransaction(Transaction trx);
    int getNextTransactionId();
    List<Transaction> readTransactionsByUserId(int userId);
}
