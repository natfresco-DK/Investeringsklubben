package CSVHandler;

import Domain.Transaction;

public interface TransactionRepository {
    void writeTransaction(Transaction trx);
    int getNextTransactionId();
}
