package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class SingleTransactionHistoryResponse {

    private TransactionHistoryClass transaction;

    public SingleTransactionHistoryResponse() {
    }

    public TransactionHistoryClass getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionHistoryClass transaction) {
        this.transaction = transaction;
    }
}
