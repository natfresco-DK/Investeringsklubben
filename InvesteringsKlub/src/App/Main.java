package App;

import Domain.Transaction;
import CSVHandler.TransactionRepository;
import CSVHandler.CSVTransactionRepository;

public class Main {
    public static void main(String[] args) {

        TransactionRepository transactionRepo = new CSVTransactionRepository();
        Transaction.readTransactionHistory(transactionRepo);
    }
}
