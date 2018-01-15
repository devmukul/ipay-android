package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory;

import java.util.List;

public class TransactionHistoryResponse {

    private List<TransactionHistory> transactionActivities;
    private boolean hasNext;

    public TransactionHistoryResponse() {
    }

    public List<TransactionHistory> getTransactions() {
        return transactionActivities;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
