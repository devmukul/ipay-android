package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import java.util.List;

public class TransactionHistoryResponse {

    private List<TransactionHistoryClass> transactions;
    private boolean hasNext;

    public TransactionHistoryResponse() {
    }

    public List<TransactionHistoryClass> getTransactions() {
        return transactions;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
