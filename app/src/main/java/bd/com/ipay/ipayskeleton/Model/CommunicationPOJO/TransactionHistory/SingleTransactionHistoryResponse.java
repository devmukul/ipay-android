package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

public class SingleTransactionHistoryResponse {

    private TransactionHistory transaction;

    public SingleTransactionHistoryResponse() {
    }

    public TransactionHistory getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionHistory transaction) {
        this.transaction = transaction;
    }
}
