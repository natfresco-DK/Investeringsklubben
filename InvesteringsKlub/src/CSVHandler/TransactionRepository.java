package CSVHandler;

import Domain.Transaction;
import java.util.*;

public interface TransactionRepository {
    void writeTransaction(Transaction trx);
    int getNextTransactionId();
    List<Transaction> getTransactionsByUserId(int userId);

}
