package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import java.util.List;

public class TransactionHistoryResponse {

    public List<TransactionHistoryClass> transactions;
    public boolean hasNext;

    public TransactionHistoryResponse() {
    }

    public List<TransactionHistoryClass> getTransactions() {
        return transactions;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
