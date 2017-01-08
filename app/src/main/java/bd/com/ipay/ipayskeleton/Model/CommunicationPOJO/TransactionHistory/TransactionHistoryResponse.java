package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import java.util.List;

public class TransactionHistoryResponse {

    private List<TransactionHistory> transactions;
    private boolean hasNext;

    public TransactionHistoryResponse() {
    }

    public List<TransactionHistory> getTransactions() {
        return transactions;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
